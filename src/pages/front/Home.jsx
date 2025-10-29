export default function Home() {
  return (
    <div className="min-h-screen bg-white text-slate-900">
      {/* Main content */}
      <main className="container mx-auto px-4 py-8 space-y-14">
        {/* 1) Hero */}
        <section aria-labelledby="hero-title" className="relative overflow-hidden rounded-2xl">
          <img
            src="https://picsum.photos/1200/500"
            alt=""
            role="presentation"
            className="h-64 w-full object-cover md:h-96"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
          <div className="absolute inset-x-0 bottom-0 p-6 md:p-10">
            <h1 id="hero-title" className="text-3xl md:text-5xl font-bold text-white">
              Fall Essentials, Fresh Arrivals
            </h1>
            <p className="mt-2 max-w-xl text-white/90 md:text-lg">
              Cozy layers, premium materials, made to last.
            </p>
            <a
              href="/category/new"
              className="inline-flex items-center rounded-xl bg-slate-900 text-white px-5 py-3 mt-4 hover:bg-slate-800"
            >
              Shop New
            </a>
          </div>
        </section>

        {/* 2) Value Props */}
        <section aria-labelledby="values">
          <h2 id="values" className="sr-only">Our values</h2>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            <div className="rounded-xl bg-slate-50 p-4">🚚 Free shipping over $50</div>
            <div className="rounded-xl bg-slate-50 p-4">↩️ 30-day free returns</div>
            <div className="rounded-xl bg-slate-50 p-4">🔒 Secure checkout</div>
          </div>
        </section>

        {/* 3) Categories */}
        <section aria-labelledby="cats">
          <div className="mb-4 flex items-end justify-between">
            <h2 id="cats" className="text-2xl font-semibold">Shop by Category</h2>
            <a href="/category/all" className="text-sm text-slate-600 hover:underline">View all</a>
          </div>

          <ul className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { title: "Men", href: "/category/men", img: "https://picsum.photos/seed/men/600/600" },
              { title: "Women", href: "/category/women", img: "https://picsum.photos/seed/women/600/600" },
              { title: "Accessories", href: "/category/accessories", img: "https://picsum.photos/seed/acc/600/600" },
              { title: "Sale", href: "/category/sale", img: "https://picsum.photos/seed/sale/600/600" },
            ].map((c) => (
              <li key={c.title} className="group overflow-hidden rounded-2xl border border-slate-200">
                <a href={c.href} className="block">
                  <img
                    src={c.img}
                    alt=""
                    className="aspect-square w-full object-cover transition group-hover:scale-[1.03]"
                  />
                  <div className="p-3">
                    <h3 className="font-medium">{c.title}</h3>
                  </div>
                </a>
              </li>
            ))}
          </ul>
        </section>

        {/* 4) Featured CTA */}
        <section aria-labelledby="feat" className="rounded-2xl border border-slate-200 overflow-hidden">
          <div className="grid md:grid-cols-2">
            <img
              src="https://picsum.photos/seed/feature/900/700"
              alt=""
              className="h-64 md:h-full w-full object-cover"
            />
            <div className="p-6 md:p-10 flex flex-col justify-center">
              <h2 id="feat" className="text-2xl font-semibold">The Wool Overshirt</h2>
              <p className="mt-2 text-slate-600">
                Soft, structured, and temperature-regulating. Your go-to layer.
              </p>
              <div className="mt-4 flex gap-2">
                <a
                  href="/product/overshirt"
                  className="rounded-xl bg-slate-900 text-white px-5 py-3 hover:bg-slate-800"
                >
                  Shop now
                </a>
                <a
                  href="/category/outerwear"
                  className="rounded-xl border px-5 py-3 hover:bg-slate-50"
                >
                  Outerwear
                </a>
              </div>
            </div>
          </div>
        </section>

        {/* 5) Testimonials */}
        <section aria-labelledby="testi">
          <h2 id="testi" className="text-2xl font-semibold">Loved by customers</h2>
          <ul className="mt-4 grid gap-4 md:grid-cols-3">
            {[
              ["“Quality is insane.”", "Ava M."],
              ["“Arrived in 2 days—perfect fit.”", "Noah K."],
              ["“My 3rd order this year.”", "Jia L."],
            ].map(([quote, name]) => (
              <li key={name} className="rounded-2xl border border-slate-200 p-4">
                <p className="text-slate-800">{quote}</p>
                <p className="mt-2 text-sm text-slate-500">— {name}</p>
              </li>
            ))}
          </ul>
        </section>

        {/* 6) FAQ */}
        <section aria-labelledby="faq">
          <h2 id="faq" className="text-2xl font-semibold">FAQ</h2>
          <dl className="mt-4 space-y-3">
            <div className="rounded-xl border border-slate-200 p-4">
              <dt className="font-medium">What’s the return policy?</dt>
              <dd className="text-slate-600 mt-1">30 days, free returns. Start from your account page.</dd>
            </div>
            <div className="rounded-xl border border-slate-200 p-4">
              <dt className="font-medium">How long is shipping?</dt>
              <dd className="text-slate-600 mt-1">2–5 business days in the US; free over $50.</dd>
            </div>
          </dl>
        </section>
      </main>
    </div>
  );
}