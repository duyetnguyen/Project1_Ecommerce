import { useEffect, useMemo, useState } from "react";
import {
  listProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  adjustStock,
  setStock,
} from "../../lib/productsApi";
/*import ProductForm from "./ProductForm";*/

/** Small, local form component kept in the same file for convenience. */
function ProductForm({ initialValues, onSubmit, onCancel, serverErrors }) {
  const [name, setName] = useState("");
  const [sku, setSku] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");        // UI string → number on submit
  const [stock, setStock] = useState("");
  const [categoryId, setCategoryId] = useState("");
  const [errors, setErrors] = useState({});      // client-side validation

  useEffect(() => {
    if (initialValues) {
      setName(initialValues.name ?? "");
      setSku(initialValues.sku ?? "");
      setDescription(initialValues.description ?? "");
      setPrice(
        initialValues.price != null
          ? String(initialValues.price)          // decimal dollars case
          // : String(initialValues.price / 100) // <-- use this if backend expects CENTS but returns integer
          : ""
      );
      setStock(initialValues.stock != null ? String(initialValues.stock) : "");
      setCategoryId(initialValues.categoryId != null ? String(initialValues.categoryId) : "");
    } else {
      setName(""); setSku(""); setDescription(""); setPrice(""); setStock(""); setCategoryId("");
    }
  }, [initialValues]);

  function validate() {
    const e = {};
    if (!name.trim()) e.name = "Name is required";
    if (!sku.trim()) e.sku = "SKU is required";
    if (price === "" || Number.isNaN(Number(price))) e.price = "Valid price required";
    if (stock === "" || Number.isNaN(Number(stock))) e.stock = "Valid stock required";
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  function handleSubmit(ev) {
    ev.preventDefault();
    if (!validate()) return;

    // Convert UI -> API payload
    const priceNumber = Number(price);           // decimal dollars case
    // const priceNumber = Math.round(Number(price) * 100); // <-- use this if backend expects CENTS

    const payload = {
      name: name.trim(),
      sku: sku.trim(),
      description: description.trim(),
      price: priceNumber,
      stock: Number(stock),
      categoryId: categoryId ? Number(categoryId) : null,
    };
    onSubmit(payload);
  }

  const fieldServerError = (field) =>
    serverErrors?.fields?.[field] ? (
      <p className="mt-1 text-sm text-red-600">{serverErrors.fields[field]}</p>
    ) : null;

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium">Name</label>
        <input
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Merino Beanie"
        />
        {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name}</p>}
        {fieldServerError("name")}
      </div>

      <div>
        <label className="block text-sm font-medium">SKU</label>
        <input
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          value={sku}
          onChange={(e) => setSku(e.target.value)}
          placeholder="BEANIE-001"
        />
        {errors.sku && <p className="mt-1 text-sm text-red-600">{errors.sku}</p>}
        {fieldServerError("sku")}
      </div>

      <div>
        <label className="block text-sm font-medium">Description</label>
        <textarea
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          rows={3}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          placeholder="Soft, warm, and breathable merino wool."
        />
        {fieldServerError("description")}
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div>
          <label className="block text-sm font-medium">Price</label>
          <input
            type="number"
            step="0.01"
            min="0"
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            placeholder="29.00"
          />
          {errors.price && <p className="mt-1 text-sm text-red-600">{errors.price}</p>}
          {fieldServerError("price")}
        </div>

        <div>
          <label className="block text-sm font-medium">Stock</label>
          <input
            type="number"
            step="1"
            min="0"
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
            value={stock}
            onChange={(e) => setStock(e.target.value)}
            placeholder="50"
          />
          {errors.stock && <p className="mt-1 text-sm text-red-600">{errors.stock}</p>}
          {fieldServerError("stock")}
        </div>

        <div>
          <label className="block text-sm font-medium">Category ID</label>
          <input
            type="number"
            step="1"
            min="0"
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
            placeholder="1"
          />
          {fieldServerError("categoryId")}
        </div>
      </div>

      {serverErrors?.message && !serverErrors?.fields && (
        <div className="rounded-xl border border-red-300 bg-red-50 p-3 text-red-700">
          {serverErrors.message}
        </div>
      )}

      <div className="flex gap-2">
        <button type="submit" className="btn">Save</button>
        <button
          type="button"
          className="btn bg-slate-200 text-slate-900 hover:bg-slate-300"
          onClick={onCancel}
        >
          Cancel
        </button>
      </div>
    </form>
  );
}

export default function AdminProductPage() {
  // table data & paging
  const [items, setItems] = useState(null);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(0);
  const size = 12;

  // UI state
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorBanner, setErrorBanner] = useState(null);
  const [editing, setEditing] = useState(null);           // { mode: "create" } or { mode: "edit", product }
  const [formServerErrors, setFormServerErrors] = useState(null);

  // Fetch list
  async function refresh({ keepPage = true } = {}) {
    try {
      setErrorBanner(null);
      const nextPage = keepPage ? page : 0;
      const { items, total, page: current } = await listProducts({
        q: search,
        page: nextPage,
        size,
        sort: "id,desc",
      });
      setItems(items);
      setTotal(total);
      setPage(current);
    } catch (e) {
      setErrorBanner(e.message || "Failed to load products");
    }
  }

  // initial & whenever page changes
  useEffect(() => { refresh({ keepPage: true }); /* eslint-disable-next-line */ }, [page]);

  // when query changes, reset page and fetch
  useEffect(() => {
    setPage(0);
    refresh({ keepPage: false });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  function openCreate() {
    setFormServerErrors(null);
    setEditing({ mode: "create" });
  }
  function openEdit(product) {
    setFormServerErrors(null);
    setEditing({ mode: "edit", product });
  }
  function closeForm() {
    setEditing(null);
    setFormServerErrors(null);
  }

  async function handleCreate(payload) {
    setLoading(true);
    try {
      await createProduct(payload);
      closeForm();
      await refresh({ keepPage: false }); // go to page 0 to show the new item
    } catch (e) {
      // Show validation errors on the form if present
      setFormServerErrors(e);
    } finally {
      setLoading(false);
    }
  }

  async function handleUpdate(id, payload) {
    setLoading(true);
    try {
      await updateProduct(id, payload);
      closeForm();
      await refresh({ keepPage: true });
    } catch (e) {
      setFormServerErrors(e);
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id) {
    if (!confirm("Delete this product?")) return;
    setLoading(true);
    try {
      await deleteProduct(id);
      await refresh({ keepPage: true });
    } catch (e) {
      setErrorBanner(e.message || "Delete failed");
    } finally {
      setLoading(false);
    }
  }

  // Stock helpers
  async function handleAdjustStock(id, delta) {
    setLoading(true);
    try {
      await adjustStock(id, delta);
      await refresh({ keepPage: true });
    } catch (e) {
      setErrorBanner(e.message || "Stock adjust failed");
    } finally {
      setLoading(false);
    }
  }

  async function handleSetStockPrompt(id, current) {
    const v = prompt("Set stock to:", String(current ?? 0));
    if (v == null) return;
    const parsed = Number(v);
    if (Number.isNaN(parsed) || parsed < 0) return alert("Enter a valid non-negative number");
    setLoading(true);
    try {
      await setStock(id, parsed);
      await refresh({ keepPage: true });
    } catch (e) {
      setErrorBanner(e.message || "Set stock failed");
    } finally {
      setLoading(false);
    }
  }

  // Pagination helpers
  const totalPages = useMemo(() => Math.max(1, Math.ceil(total / size)), [total, size]);
  const canPrev = page > 0;
  const canNext = page + 1 < totalPages;

  return (
    <div className="container py-8 space-y-6">
      <header className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <h1 className="text-2xl font-semibold">Admin · Products</h1>
        <div className="flex gap-2">
          <input
            className="w-64 rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
            placeholder="Search by name or SKU"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <button className="btn" onClick={openCreate}>+ New Product</button>
        </div>
      </header>

      {errorBanner && (
        <div className="rounded-xl border border-red-300 bg-red-50 p-3 text-red-700">
          {errorBanner}
        </div>
      )}

      {!items ? (
        <p>Loading…</p>
      ) : items.length ? (
        <div className="space-y-3">
          <div className="overflow-x-auto rounded-2xl border border-slate-200">
            <table className="min-w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="px-4 py-2">Name</th>
                  <th className="px-4 py-2">SKU</th>
                  <th className="px-4 py-2">Price</th>
                  <th className="px-4 py-2">Stock</th>
                  <th className="px-4 py-2">Category</th>
                  <th className="px-4 py-2 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map((p) => (
                  <tr key={p.id} className="border-t">
                    <td className="px-4 py-2">{p.name}</td>
                    <td className="px-4 py-2 text-slate-600">{p.sku}</td>
                    <td className="px-4 py-2">
                      {/* decimal dollars */}
                      ${Number(p.price).toFixed(2)}
                      {/* cents version: ${(p.price / 100).toFixed(2)} */}
                    </td>
                    <td className="px-4 py-2">
                      <div className="flex items-center gap-2">
                        <span>{p.stock}</span>
                        <div className="flex gap-1">
                          <button
                            className="rounded-lg border border-slate-300 px-2 py-1 text-xs hover:bg-slate-50"
                            onClick={() => handleAdjustStock(p.id, +1)}
                            title="Add 1"
                          >
                            +1
                          </button>
                          <button
                            className="rounded-lg border border-slate-300 px-2 py-1 text-xs hover:bg-slate-50"
                            onClick={() => handleAdjustStock(p.id, -1)}
                            title="Subtract 1"
                          >
                            -1
                          </button>
                          <button
                            className="rounded-lg border border-slate-300 px-2 py-1 text-xs hover:bg-slate-50"
                            onClick={() => handleSetStockPrompt(p.id, p.stock)}
                            title="Set exact stock"
                          >
                            Set
                          </button>
                        </div>
                      </div>
                    </td>
                    <td className="px-4 py-2">{p.categoryId ?? "—"}</td>
                    <td className="px-4 py-2">
                      <div className="flex justify-end gap-2">
                        <button className="btn" onClick={() => openEdit(p)}>Edit</button>
                        <button
                          className="btn bg-red-600 hover:bg-red-700"
                          onClick={() => handleDelete(p.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {loading && <div className="p-3 text-sm text-slate-500">Saving…</div>}
          </div>

          {/* Pager */}
          <div className="flex items-center justify-between">
            <div className="text-sm text-slate-600">
              Page <strong>{page + 1}</strong> of <strong>{totalPages}</strong> · {total} total
            </div>
            <div className="flex gap-2">
              <button
                className="btn bg-slate-200 text-slate-900 hover:bg-slate-300 disabled:opacity-50"
                disabled={!canPrev}
                onClick={() => canPrev && setPage((p) => p - 1)}
              >
                ← Prev
              </button>
              <button
                className="btn bg-slate-200 text-slate-900 hover:bg-slate-300 disabled:opacity-50"
                disabled={!canNext}
                onClick={() => canNext && setPage((p) => p + 1)}
              >
                Next →
              </button>
            </div>
          </div>
        </div>
      ) : (
        <p>No products found.</p>
      )}

      {/* Inline drawer/card for Create/Edit */}
      {editing && (
        <div className="rounded-2xl border border-slate-200 bg-white p-4 shadow">
          <div className="mb-3 flex items-center justify-between">
            <h2 className="text-lg font-semibold">
              {editing.mode === "create" ? "Create Product" : "Edit Product"}
            </h2>
            <button className="text-slate-500 hover:underline" onClick={closeForm}>Close</button>
          </div>

          <ProductForm
            initialValues={editing.product}
            onCancel={closeForm}
            serverErrors={formServerErrors}
            onSubmit={(values) =>
              editing.mode === "create"
                ? handleCreate(values)
                : handleUpdate(editing.product.id, values)
            }
          />
        </div>
      )}
    </div>
  );
}
