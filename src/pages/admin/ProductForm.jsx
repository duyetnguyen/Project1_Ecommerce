import { useEffect, useState } from "react";

export default function ProductForm({ initialValues, onSubmit, onCancel }) {
  const [title, setTitle] = useState("");
  const [price, setPrice] = useState(""); // dollars in the input
  const [image, setImage] = useState("");
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initialValues) {
      setTitle(initialValues.title ?? "");
      setPrice(initialValues.price != null ? (initialValues.price / 100).toFixed(2) : "");
      setImage(initialValues.image ?? "");
    }
  }, [initialValues]);

  function validate() {
    const e = {};
    if (!title.trim()) e.title = "Title is required";
    const numeric = Number(price);
    if (Number.isNaN(numeric) || numeric <= 0) e.price = "Enter a positive price";
    if (!image.trim()) e.image = "Image URL is required";
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (!validate()) return;
    const cents = Math.round(Number(price) * 100);
    onSubmit({ title: title.trim(), price: cents, image: image.trim() });
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label className="block text-sm font-medium">Title</label>
        <input
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Merino Beanie"
        />
        {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium">Price (USD)</label>
        <input
          type="number"
          step="0.01"
          min="0"
          className="mt-1 w-40 rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          value={price}
          onChange={(e) => setPrice(e.target.value)}
          placeholder="49.00"
        />
        {errors.price && <p className="mt-1 text-sm text-red-600">{errors.price}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium">Image URL</label>
        <input
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-slate-400"
          value={image}
          onChange={(e) => setImage(e.target.value)}
          placeholder="https://…"
        />
        {errors.image && <p className="mt-1 text-sm text-red-600">{errors.image}</p>}
        {image && (
          <div className="mt-3">
            <img src={image} alt="" className="h-28 w-28 rounded-lg object-cover" />
          </div>
        )}
      </div>

      <div className="flex gap-2">
        <button type="submit" className="btn">Save</button>
        <button type="button" className="btn bg-slate-200 text-slate-900 hover:bg-slate-300" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
}