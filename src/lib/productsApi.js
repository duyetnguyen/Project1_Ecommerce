import http from "./http";

/**
 * The backend ProductResponse likely includes:
 * { id, name, sku, description, price, stock, categoryId, ...timestamps }
 * We'll keep our UI flexible and map `name`->`title` for display where needed.
 */

export async function listProducts({ q = "", page = 0, size = 12, sort = "id,desc" } = {}) {
  const params = {};
  if (q) params.q = q;
  params.page = page;
  params.size = size;
  if (sort) params.sort = sort;
  const res = await http.get("/api/products", { params });
  // Spring Page<T> usually returns { content, totalElements, number, size, ... }
  const data = res.data;
  const items = data.content ?? data.items ?? []; // support either Page or custom wrapper
  return {
    items,
    total: data.totalElements ?? data.total ?? items.length,
    page: data.number ?? page,
    size: data.size ?? size,
  };
}

export async function getProductById(id) {
  const { data } = await http.get(`/api/products/${id}`);
  return data;
}

export async function getProductBySku(sku) {
  // returns 200 with product or throws {status:404}
  const res = await http.get(`/api/products/by-sku/${encodeURIComponent(sku)}`);
  return res.data;
}

export async function createProduct(payload) {
  // payload must match CreateProductRequest (name, sku, description, price, stock, categoryId)
  const res = await http.post("/api/products", payload);
  // 201 + Location header + body (your controller returns body)
  return res.data;
}

export async function updateProduct(id, payload) {
  const { data } = await http.put(`/api/products/${id}`, payload);
  return data;
}

export async function deleteProduct(id) {
  await http.delete(`/api/products/${id}`);
  return { ok: true };
}

export async function adjustStock(id, delta) {
  const { data } = await http.patch(`/api/products/${id}/stock/adjust`, { delta });
  return data;
}

export async function setStock(id, stock) {
  const { data } = await http.put(`/api/products/${id}/stock`, { stock });
  return data;
}