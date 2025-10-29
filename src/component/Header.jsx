export default function Header({cartCount = 0}){
    return (
        <header>
      <a href="/">Brand</a>
      <nav>
        <a href="/category/men">Men</a>
        <a href="/category/women">Women</a>
      </nav>
      <a href="/cart" aria-label={`Cart has ${cartCount} items`}>
        🛒 {cartCount}
      </a>
    </header>
  );
    
}