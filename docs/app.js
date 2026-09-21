/**
 * TileIQ Web Edition - Modern Showroom Intelligence & Dead-Stock Platform
 * Designed for standalone execution on GitHub Pages, Desktop & Mobile (iOS & Android)
 */

// Initial Seed Inventory matching Android Room Database
const DEFAULT_TILES = [
  {
    id: "TIQ-001",
    sku: "KAJ-STAT-2X4-01",
    brand: "Kajaria",
    tileName: "Statuario Royale White Polished",
    size: "2x4 ft (60x120 cm)",
    finish: "High Gloss Polish",
    category: "GVT",
    boxes: 420,
    sqftPerBox: 15.5,
    mrp: 1450,
    price: 1250,
    cost: 820,
    ageDays: 28,
    warehouse: "Central Depot A",
    image: "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&auto=format&fit=crop"
  },
  {
    id: "TIQ-002",
    sku: "SOM-MARB-2X2-02",
    brand: "Somany",
    tileName: "Crema Marfil Italian Satin",
    size: "2x2 ft (60x60 cm)",
    finish: "Satin Matt",
    category: "GVT",
    boxes: 280,
    sqftPerBox: 16.0,
    mrp: 1100,
    price: 890,
    cost: 580,
    ageDays: 95, // Attention > 90d
    warehouse: "Central Depot A",
    image: "https://images.unsplash.com/photo-1552321554-5fefe8c9ef14?w=600&auto=format&fit=crop"
  },
  {
    id: "TIQ-003",
    sku: "NIT-SLAT-2X2-03",
    brand: "Nitco",
    tileName: "Slate Grey Anti-Skid 2x2",
    size: "2x2 ft (60x60 cm)",
    finish: "Anti-Skid",
    category: "Outdoor",
    boxes: 210,
    sqftPerBox: 15.5,
    mrp: 1200,
    price: 980,
    cost: 620,
    ageDays: 125, // Attention > 90d
    warehouse: "West Depot B",
    image: "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop"
  },
  {
    id: "TIQ-004",
    sku: "AGL-WOOD-1X4-04",
    brand: "Asian Granito",
    tileName: "Rustic Oak Wood Plank 8x40 inch",
    size: "1x3.3 ft (20x100 cm)",
    finish: "Rustic Matt",
    category: "Wood",
    boxes: 350,
    sqftPerBox: 10.76,
    mrp: 1450,
    price: 1150,
    cost: 710,
    ageDays: 210, // Dead-Stock > 180d (High Risk)
    warehouse: "Central Depot A",
    image: "https://images.unsplash.com/photo-1533090161767-e6ffed986c88?w=600&auto=format&fit=crop"
  },
  {
    id: "TIQ-005",
    sku: "SIM-SUBW-1X1-05",
    brand: "Simpolo",
    tileName: "Artisan Moroccan Subway Teal",
    size: "1x1 ft (30x30 cm)",
    finish: "Glossy Handmade",
    category: "Wall",
    boxes: 190,
    sqftPerBox: 9.69,
    mrp: 950,
    price: 780,
    cost: 490,
    ageDays: 195, // Dead-Stock > 180d (High Risk)
    warehouse: "North Depot C",
    image: "https://images.unsplash.com/photo-1574359411659-15573a27fd0c?w=600&auto=format&fit=crop"
  },
  {
    id: "TIQ-006",
    sku: "ORI-ONYX-2X4-06",
    brand: "Orient Bell",
    tileName: "Onyx Ice Translucent Polish",
    size: "2x4 ft (60x120 cm)",
    finish: "Ultra Gloss",
    category: "GVT",
    boxes: 190,
    sqftPerBox: 15.5,
    mrp: 1650,
    price: 1390,
    cost: 890,
    ageDays: 18, // Fresh
    warehouse: "Central Depot A",
    image: "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=600&auto=format&fit=crop"
  }
];

// Application State
let appState = {
  tiles: JSON.parse(localStorage.getItem('tileiq_tiles')) || DEFAULT_TILES,
  cart: JSON.parse(localStorage.getItem('tileiq_cart')) || [
    { tileId: "TIQ-001", boxes: 24, discountPct: 0 },
    { tileId: "TIQ-004", boxes: 35, discountPct: 20 } // 20% dead-stock clearance discount
  ],
  selectedCycleMonths: 1,
  selectedCycleAmount: 5000,
  activeFilter: 'all',
  searchQuery: ''
};

// VPA Configuration
const SHOWROOM_UPI_VPA = "gupta.ceramics@okhdfcbank";
const SHOWROOM_NAME = "Gupta Ceramics and Tiles";
const LICENSE_UPI_VPA = "tileiq.licensing@icici";

// Formatting Currency (₹ INR)
function formatINR(val) {
  return "₹" + Math.round(val).toLocaleString('en-IN');
}

// Initial Boot
document.addEventListener('DOMContentLoaded', () => {
  renderDashboard();
  renderInventory();
  renderQuotation();
  setupFilterChips();
  setupSearch();
  setupEventListeners();
});

// View Navigation
function switchTab(viewId, filterMode = null) {
  document.querySelectorAll('.view-panel').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));

  const targetPanel = document.getElementById(`view-${viewId}`);
  if (targetPanel) {
    targetPanel.classList.add('active');
  }

  // Update mobile bottom nav + desktop side nav via shared data-view attributes
  document.querySelectorAll('[data-view]').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.view === viewId);
  });

  if (viewId === 'inventory' && filterMode === 'deadstock') {
    applyCategoryFilter('deadstock');
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// -------------------------------------------------------------
// DASHBOARD LOGIC
// -------------------------------------------------------------
function renderDashboard() {
  let totalValue = 0;
  let totalBoxes = 0;
  let deadStockValue = 0;
  let deadStockBatches = 0;

  appState.tiles.forEach(t => {
    const batchVal = t.boxes * t.price;
    totalValue += batchVal;
    totalBoxes += t.boxes;

    if (t.ageDays >= 90) {
      deadStockValue += batchVal;
      deadStockBatches++;
    }
  });

  const recoverable = deadStockValue * 0.82; // average 18% clearance recovery

  document.getElementById('kpiTotalValue').textContent = formatINR(totalValue);
  document.getElementById('kpiDeadStock').textContent = formatINR(deadStockValue);
  document.getElementById('kpiRecoverable').textContent = formatINR(recoverable);

  // Render Dead Stock alert list
  const listEl = document.getElementById('dashboardDeadStockList');
  listEl.innerHTML = '';

  const agingTiles = appState.tiles.filter(t => t.ageDays >= 90);
  agingTiles.forEach(tile => {
    const isCritical = tile.ageDays >= 180;
    const badgeClass = isCritical ? 'badge-aging-high' : 'badge-aging-warn';
    const recDiscount = isCritical ? 25 : 15;

    const row = document.createElement('div');
    row.className = 'deadstock-item';
    row.innerHTML = `
      <div class="deadstock-item-info">
        <img src="${tile.image}" alt="${tile.tileName}" class="tile-thumb-sm">
        <div class="deadstock-meta">
          <strong>${tile.tileName} (${tile.size})</strong>
          <span>${tile.boxes} Boxes Available • ${tile.warehouse}</span>
        </div>
      </div>
      <div style="display: flex; align-items: center; gap: 10px;">
        <span class="${badgeClass}">${tile.ageDays} Days In Stock</span>
        <button class="btn btn-outline-sm" onclick="addAgingTileToQuote('${tile.id}', ${recDiscount})">
          <i class="fa-solid fa-tags"></i> Apply ${recDiscount}% Off
        </button>
      </div>
    `;
    listEl.appendChild(row);
  });
}

function addAgingTileToQuote(tileId, recDiscount) {
  const existing = appState.cart.find(c => c.tileId === tileId);
  if (existing) {
    existing.discountPct = recDiscount;
  } else {
    appState.cart.push({ tileId, boxes: 20, discountPct: recDiscount });
  }
  saveCart();
  renderQuotation();
  switchTab('quotation');
  showToast(`Added with ${recDiscount}% clearance discount!`);
}

// -------------------------------------------------------------
// INVENTORY LOGIC
// -------------------------------------------------------------
function renderInventory() {
  const grid = document.getElementById('tilesGrid');
  grid.innerHTML = '';

  const filtered = appState.tiles.filter(tile => {
    const matchesSearch = 
      tile.tileName.toLowerCase().includes(appState.searchQuery.toLowerCase()) ||
      tile.brand.toLowerCase().includes(appState.searchQuery.toLowerCase()) ||
      tile.sku.toLowerCase().includes(appState.searchQuery.toLowerCase()) ||
      tile.finish.toLowerCase().includes(appState.searchQuery.toLowerCase());

    if (!matchesSearch) return false;

    if (appState.activeFilter === 'all') return true;
    if (appState.activeFilter === 'deadstock') return tile.ageDays >= 90;
    return tile.category.toLowerCase() === appState.activeFilter.toLowerCase();
  });

  document.getElementById('inventoryCountText').textContent = `Showing ${filtered.length} tiles`;

  filtered.forEach(tile => {
    let agingBadge = '';
    if (tile.ageDays >= 180) {
      agingBadge = `<span class="badge-aging-high tile-aging-badge-float">${tile.ageDays}d Dead-Stock</span>`;
    } else if (tile.ageDays >= 90) {
      agingBadge = `<span class="badge-aging-warn tile-aging-badge-float">${tile.ageDays}d Slow-Moving</span>`;
    }

    const card = document.createElement('div');
    card.className = 'tile-card';
    card.innerHTML = `
      <div class="tile-image-wrap">
        <img src="${tile.image}" alt="${tile.tileName}">
        ${agingBadge}
      </div>
      <div class="tile-card-body">
        <span class="tile-card-brand">${tile.brand} • ${tile.category}</span>
        <h4 class="tile-card-title">${tile.tileName}</h4>
        <div class="tile-card-specs">
          <span>${tile.size}</span>
          <span>${tile.finish}</span>
          <span>${tile.sqftPerBox} sqft/box</span>
        </div>
        <div class="tile-card-pricing">
          <div>
            <div class="mrp-tag">${formatINR(tile.mrp)} MRP</div>
            <div class="price-tag">${formatINR(tile.price)} <small style="font-size: 11px; font-weight: normal; color: var(--slate-400)">/ box</small></div>
          </div>
          <button class="btn btn-outline-sm" onclick="addTileToCart('${tile.id}')">
            <i class="fa-solid fa-plus"></i> Add to Quote
          </button>
        </div>
      </div>
    `;
    grid.appendChild(card);
  });
}

function setupFilterChips() {
  document.querySelectorAll('#categoryFilterChips .chip').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('#categoryFilterChips .chip').forEach(c => c.classList.remove('active'));
      btn.classList.add('active');
      appState.activeFilter = btn.dataset.cat;
      renderInventory();
    });
  });
}

function applyCategoryFilter(cat) {
  appState.activeFilter = cat;
  document.querySelectorAll('#categoryFilterChips .chip').forEach(c => {
    c.classList.toggle('active', c.dataset.cat === cat);
  });
  renderInventory();
}

function setupSearch() {
  const input = document.getElementById('tileSearchInput');
  input.addEventListener('input', (e) => {
    appState.searchQuery = e.target.value.trim();
    renderInventory();
  });
}

function addTileToCart(tileId) {
  const existing = appState.cart.find(c => c.tileId === tileId);
  if (existing) {
    existing.boxes += 5;
  } else {
    appState.cart.push({ tileId, boxes: 10, discountPct: 0 });
  }
  saveCart();
  renderQuotation();
  showToast("Tile added to quotation!");
}

// -------------------------------------------------------------
// QUOTATION BUILDER LOGIC
// -------------------------------------------------------------
function renderQuotation() {
  const container = document.getElementById('quoteItemsList');
  container.innerHTML = '';
  document.getElementById('cartBadge').textContent = appState.cart.length;
  document.getElementById('cartBadgeSide').textContent = appState.cart.length;

  if (appState.cart.length === 0) {
    container.innerHTML = `
      <div style="text-align: center; padding: 40px 20px; color: var(--slate-400);">
        <i class="fa-solid fa-cart-shopping" style="font-size: 32px; margin-bottom: 12px; opacity: 0.5;"></i>
        <p>No tile batches added yet.</p>
        <button class="btn btn-outline-sm mt-3" onclick="switchTab('inventory')">Browse Catalog</button>
      </div>
    `;
    updateQuoteSummary(0, 0);
    return;
  }

  let subtotal = 0;
  let totalDiscount = 0;

  appState.cart.forEach((item, index) => {
    const tile = appState.tiles.find(t => t.id === item.tileId);
    if (!tile) return;

    const baseItemTotal = item.boxes * tile.price;
    const discountAmt = baseItemTotal * (item.discountPct / 100);
    const finalItemTotal = baseItemTotal - discountAmt;

    subtotal += baseItemTotal;
    totalDiscount += discountAmt;

    const totalSqft = (item.boxes * tile.sqftPerBox).toFixed(1);

    const row = document.createElement('div');
    row.className = 'quote-item-row';
    row.innerHTML = `
      <div class="quote-item-details">
        <strong>${tile.tileName}</strong>
        <span>${tile.size} • ${totalSqft} sq.ft (${item.boxes} Boxes @ ${formatINR(tile.price)}/box)</span>
        ${item.discountPct > 0 ? `<span class="text-emerald" style="display: block; font-weight: 700;">${item.discountPct}% Dead-Stock Clearance Discount</span>` : ''}
      </div>
      <div class="qty-control">
        <button class="qty-btn" onclick="updateItemBoxes(${index}, -1)">-</button>
        <input type="number" class="qty-input" value="${item.boxes}" onchange="setItemBoxes(${index}, this.value)">
        <button class="qty-btn" onclick="updateItemBoxes(${index}, 1)">+</button>
      </div>
      <div style="text-align: right;">
        <strong style="display: block; color: #fff;">${formatINR(finalItemTotal)}</strong>
        <button style="background: transparent; border: none; color: var(--rose-500); cursor: pointer; font-size: 12px; margin-top: 4px;" onclick="removeCartItem(${index})">
          <i class="fa-solid fa-trash"></i> Remove
        </button>
      </div>
    `;
    container.appendChild(row);
  });

  updateQuoteSummary(subtotal, totalDiscount);
}

function updateQuoteSummary(subtotal, totalDiscount) {
  const freight = parseFloat(document.getElementById('sumFreight').value) || 0;
  const taxable = Math.max(0, subtotal - totalDiscount);
  const gst = taxable * 0.18; // 18% Ceramic GST
  const netTotal = taxable + gst + freight;

  document.getElementById('sumSubtotal').textContent = formatINR(subtotal);
  document.getElementById('sumDiscount').textContent = `- ${formatINR(totalDiscount)}`;
  document.getElementById('sumGst').textContent = formatINR(gst);
  document.getElementById('sumNetTotal').textContent = formatINR(netTotal);
}

function updateItemBoxes(index, delta) {
  if (appState.cart[index]) {
    appState.cart[index].boxes = Math.max(1, appState.cart[index].boxes + delta);
    saveCart();
    renderQuotation();
  }
}

function setItemBoxes(index, val) {
  const parsed = parseInt(val) || 1;
  if (appState.cart[index]) {
    appState.cart[index].boxes = Math.max(1, parsed);
    saveCart();
    renderQuotation();
  }
}

function removeCartItem(index) {
  appState.cart.splice(index, 1);
  saveCart();
  renderQuotation();
}

function saveCart() {
  localStorage.setItem('tileiq_cart', JSON.stringify(appState.cart));
}

// -------------------------------------------------------------
// WHATSAPP & UPI SHARING LOGIC
// -------------------------------------------------------------
function shareViaWhatsApp() {
  const custName = document.getElementById('custName').value.trim() || "Customer";
  const custPhone = document.getElementById('custPhone').value.trim().replace(/\D/g, '');
  const custSite = document.getElementById('custSite').value.trim();
  const netTotal = document.getElementById('sumNetTotal').textContent;

  if (appState.cart.length === 0) {
    showToast("Please add at least one tile to generate quotation");
    return;
  }

  let itemsText = "";
  appState.cart.forEach((item, idx) => {
    const tile = appState.tiles.find(t => t.id === item.tileId);
    if (tile) {
      const sqft = (item.boxes * tile.sqftPerBox).toFixed(0);
      itemsText += `${idx + 1}. *${tile.tileName}* (${tile.size})\n   • ${item.boxes} Boxes (${sqft} Sq.Ft) @ ₹${tile.price}/box\n`;
    }
  });

  const message = 
`*ESTIMATE & QUOTATION - GUPTA CERAMICS & TILES*
Date: ${new Date().toLocaleDateString('en-GB')}
Estimate Ref: TIQ-QT-${Math.floor(1000 + Math.random() * 9000)}

*Client Details:*
Name: ${custName}
Project: ${custSite || "Site Delivery"}

*Materials Selected:*
${itemsText}
----------------------------------------
*Net Total Payable:* ${netTotal}
*(Inclusive of 18% GST & Freight)*

*Payment Terms:* 50% Advance via Google Pay / UPI
UPI ID: ${SHOWROOM_UPI_VPA}

_Generated via TileIQ Showroom Intelligence_`;

  const encodedMsg = encodeURIComponent(message);
  const waUrl = custPhone ? `https://wa.me/${custPhone}?text=${encodedMsg}` : `https://wa.me/?text=${encodedMsg}`;
  
  window.open(waUrl, '_blank');
}

function triggerCustomerGPay() {
  const netTotalText = document.getElementById('sumNetTotal').textContent;
  const netTotal = parseFloat(netTotalText.replace(/[^\d.]/g, '')) || 0;
  
  if (netTotal === 0) {
    showToast("Quotation total is ₹0");
    return;
  }

  const advanceAmt = Math.round(netTotal * 0.5); // 50% deposit
  const upiUrl = `upi://pay?pa=${SHOWROOM_UPI_VPA}&pn=${encodeURIComponent(SHOWROOM_NAME)}&am=${advanceAmt}&cu=INR&tn=${encodeURIComponent("Tile Quotation Advance")}`;

  // On Mobile (iOS / Android), window.location.href attempts native deep link opening
  window.location.href = upiUrl;
  
  // Also show QR fallback modal
  showUpiQrModal(advanceAmt);
}

function showUpiQrModal(specificAmount = null) {
  const netTotalText = document.getElementById('sumNetTotal').textContent;
  const netTotal = parseFloat(netTotalText.replace(/[^\d.]/g, '')) || 0;
  const amount = specificAmount || Math.round(netTotal * 0.5) || 5000;

  const upiString = `upi://pay?pa=${SHOWROOM_UPI_VPA}&pn=${encodeURIComponent(SHOWROOM_NAME)}&am=${amount}&cu=INR&tn=${encodeURIComponent("Deposit for Tiles")}`;
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(upiString)}`;

  document.getElementById('qrAmountDisplay').textContent = formatINR(amount);
  document.getElementById('customerQrImg').src = qrApiUrl;
  openModal('qrModal');
}

// -------------------------------------------------------------
// SHOWROOM PRO SUBSCRIPTION MODAL (₹5,000 / month)
// -------------------------------------------------------------
function openSubscriptionModal() {
  selectSubCycle(1, 5000, document.querySelector('.plan-option'));
  openModal('paymentModal');
}

function selectSubCycle(months, amount, element) {
  appState.selectedCycleMonths = months;
  appState.selectedCycleAmount = amount;

  document.querySelectorAll('.plan-option').forEach(el => el.classList.remove('active'));
  if (element) element.classList.add('active');

  const upiString = `upi://pay?pa=${LICENSE_UPI_VPA}&pn=${encodeURIComponent("TileIQ Pro Licensing")}&am=${amount}&cu=INR&tn=${encodeURIComponent("TileIQ Pro " + months + " Mo")}`;
  const qrApiUrl = `https://api.qrserver.com/v1/create-qr-code/?size=240x240&data=${encodeURIComponent(upiString)}`;

  document.getElementById('subQrImage').src = qrApiUrl;
  document.getElementById('gpayDeepLink').href = upiString;
}

function confirmSubscriptionPayment() {
  const payerUpi = document.getElementById('payerUpiIdInput').value || "user@upi";
  const invNum = "INV-2026-" + Math.floor(1000 + Math.random() * 9000);
  const dateStr = new Date().toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });

  const table = document.getElementById('invoiceTableBody');
  const newRow = document.createElement('tr');
  newRow.innerHTML = `
    <td>${invNum}</td>
    <td>${dateStr}</td>
    <td>${appState.selectedCycleMonths} Month Pro</td>
    <td>${formatINR(appState.selectedCycleAmount)}</td>
    <td><span class="badge-paid">PAID (${payerUpi})</span></td>
  `;
  table.prepend(newRow);

  closeModal('paymentModal');
  showToast(`Subscription activated for ${appState.selectedCycleMonths} Mo! ₹${appState.selectedCycleAmount} (Incl. Taxes)`);
}

// -------------------------------------------------------------
// MODALS & HELPERS
// -------------------------------------------------------------
function openModal(id) {
  document.getElementById(id)?.classList.add('active');
}

function closeModal(id) {
  document.getElementById(id)?.classList.remove('active');
}

function openAddTileModal() {
  showToast("Tile batch add module opened");
}

function showToast(msg) {
  const toast = document.getElementById('toast');
  toast.textContent = msg;
  toast.classList.add('show');
  setTimeout(() => {
    toast.classList.remove('show');
  }, 2800);
}

function setupEventListeners() {
  document.getElementById('sumFreight').addEventListener('input', () => {
    renderQuotation();
  });
}
