#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
KDF-Tools · Market Intelligence & Paper Trading Radar
Cotizaciones en vivo, alertas de precios y simulador de inversiones sin riesgo.
"""

import os
import sys
import json
import time
import subprocess
import urllib.request
from typing import Dict, Any, List

STATE_DIR = os.path.expanduser("~/.kdf-studio")
ALERTS_FILE = os.path.join(STATE_DIR, "market_alerts.json")
PORTFOLIO_FILE = os.path.join(STATE_DIR, "paper_portfolio.json")
MARKET_LOG = "/data/data/com.termux/files/home/KDF-LuX/logs/market_radar.log"

os.makedirs(STATE_DIR, exist_ok=True)
os.makedirs(os.path.dirname(MARKET_LOG), exist_ok=True)

DEFAULT_ASSETS = [
    {"symbol": "^GSPC", "name": "S&P 500", "type": "index"},
    {"symbol": "^IXIC", "name": "NASDAQ", "type": "index"},
    {"symbol": "BTC-USD", "name": "Bitcoin", "type": "crypto"},
    {"symbol": "ETH-USD", "name": "Ethereum", "type": "crypto"},
    {"symbol": "GC=F", "name": "Oro", "type": "commodity"},
    {"symbol": "SI=F", "name": "Plata", "type": "commodity"},
    {"symbol": "NVDA", "name": "Nvidia", "type": "stock"},
    {"symbol": "AAPL", "name": "Apple", "type": "stock"},
    {"symbol": "TSLA", "name": "Tesla", "type": "stock"}
]

def log(msg: str):
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    formatted = f"[{timestamp}] [MARKET] {msg}"
    print(formatted)
    try:
        with open(MARKET_LOG, "a", encoding="utf-8") as f:
            f.write(formatted + "\n")
    except Exception:
        pass

def send_market_notification(title: str, content: str):
    try:
        cmd = [
            "/data/data/com.termux/files/usr/bin/termux-notification",
            "--id", "8801",
            "--title", title,
            "--content", content,
            "--priority", "high",
            "--vibrate", "300,100,300"
        ]
        subprocess.run(cmd, capture_output=True, timeout=5)
    except Exception as e:
        log(f"Error al enviar notificación de mercado: {e}")

def fetch_quote(symbol: str) -> Dict[str, Any]:
    url = f"https://query1.finance.yahoo.com/v8/finance/chart/{urllib.parse.quote(symbol)}?interval=1d&range=5d"
    req = urllib.request.Request(
        url,
        headers={"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
    )
    try:
        with urllib.request.urlopen(req, timeout=6) as response:
            data = json.loads(response.read().decode("utf-8"))
            result = data.get("chart", {}).get("result", [])
            if not result:
                return {"symbol": symbol, "error": "No data returned"}
            
            meta = result[0].get("meta", {})
            current_price = meta.get("regularMarketPrice", 0.0)
            prev_close = meta.get("chartPreviousClose", meta.get("previousClose", current_price))
            
            change = current_price - prev_close if prev_close else 0.0
            change_pct = (change / prev_close * 100.0) if prev_close else 0.0
            
            return {
                "symbol": symbol,
                "currency": meta.get("currency", "USD"),
                "price": round(current_price, 2 if current_price > 1 else 4),
                "change": round(change, 2 if abs(change) > 1 else 4),
                "change_pct": round(change_pct, 2),
                "high_24h": meta.get("regularMarketDayHigh", current_price),
                "low_24h": meta.get("regularMarketDayLow", current_price),
                "timestamp": meta.get("regularMarketTime", int(time.time()))
            }
    except Exception as e:
        return {"symbol": symbol, "error": str(e), "price": 0.0, "change_pct": 0.0}

def fetch_all_quotes() -> List[Dict[str, Any]]:
    quotes = []
    for item in DEFAULT_ASSETS:
        q = fetch_quote(item["symbol"])
        q["name"] = item["name"]
        q["type"] = item["type"]
        quotes.append(q)
    return quotes

# ── Sistema de Alertas de Precios ─────────────────────────────────────────────

def load_alerts() -> List[Dict[str, Any]]:
    if not os.path.exists(ALERTS_FILE):
        default_alerts = [
            {"id": 1, "symbol": "BTC-USD", "name": "Bitcoin", "condition": "above", "target": 75000.0, "triggered": False},
            {"id": 2, "symbol": "GC=F", "name": "Oro", "condition": "above", "target": 2800.0, "triggered": False}
        ]
        save_alerts(default_alerts)
        return default_alerts
    try:
        with open(ALERTS_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception:
        return []

def save_alerts(alerts: List[Dict[str, Any]]):
    with open(ALERTS_FILE, "w", encoding="utf-8") as f:
        json.dump(alerts, f, indent=2)

def check_price_alerts():
    alerts = load_alerts()
    updated = False
    for alert in alerts:
        if alert.get("triggered"):
            continue
        q = fetch_quote(alert["symbol"])
        current_price = q.get("price", 0.0)
        if current_price <= 0:
            continue
        
        condition = alert.get("condition")
        target = alert.get("target", 0.0)
        
        hit = False
        if condition == "above" and current_price >= target:
            hit = True
        elif condition == "below" and current_price <= target:
            hit = True
            
        if hit:
            alert["triggered"] = True
            alert["triggered_at"] = time.strftime("%Y-%m-%d %H:%M:%S")
            updated = True
            title = f"📈 ALERTA BURSÁTIL: {alert.get('name', alert['symbol'])}"
            msg = f"{alert['symbol']} alcanzó ${current_price:,.2f} ({condition} de ${target:,.2f})"
            log(f"Alerta disparada: {msg}")
            send_market_notification(title, msg)
            
    if updated:
        save_alerts(alerts)
    return alerts

# ── Simulador de Inversiones (Paper Trading) ──────────────────────────────────

def init_portfolio():
    if not os.path.exists(PORTFOLIO_FILE):
        initial = {
            "cash_usd": 1000.00,
            "initial_cash": 1000.00,
            "created_at": time.strftime("%Y-%m-%d %H:%M:%S"),
            "disclaimer": "MODO SIMULACIÓN - Fondos virtuales para pruebas y aprendizaje sin riesgo financiero real.",
            "positions": {},
            "trade_history": []
        }
        with open(PORTFOLIO_FILE, "w", encoding="utf-8") as f:
            json.dump(initial, f, indent=2)
        return initial
    try:
        with open(PORTFOLIO_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception:
        return {"cash_usd": 1000.0, "positions": {}, "trade_history": []}

def save_portfolio(p: Dict[str, Any]):
    with open(PORTFOLIO_FILE, "w", encoding="utf-8") as f:
        json.dump(p, f, indent=2)

def paper_buy(symbol: str, amount_usd: float) -> Dict[str, Any]:
    p = init_portfolio()
    if p["cash_usd"] < amount_usd:
        return {"success": False, "error": f"Saldo insuficiente. Saldo disponible: ${p['cash_usd']:.2f}"}
    
    quote = fetch_quote(symbol)
    price = quote.get("price", 0.0)
    if price <= 0:
        return {"success": False, "error": f"No se pudo obtener precio en tiempo real para {symbol}"}
    
    shares = amount_usd / price
    p["cash_usd"] -= amount_usd
    
    pos = p["positions"].get(symbol, {"shares": 0.0, "total_invested": 0.0})
    pos["shares"] += shares
    pos["total_invested"] += amount_usd
    pos["avg_price"] = pos["total_invested"] / pos["shares"]
    p["positions"][symbol] = pos
    
    trade = {
        "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
        "type": "BUY",
        "symbol": symbol,
        "shares": round(shares, 6),
        "price": price,
        "amount_usd": amount_usd
    }
    p["trade_history"].append(trade)
    save_portfolio(p)
    log(f"Paper Buy: Comprado {shares:.4f} de {symbol} a ${price:,.2f} (${amount_usd:.2f} USD)")
    return {"success": True, "trade": trade, "portfolio": p}

def paper_sell(symbol: str, shares_to_sell: float) -> Dict[str, Any]:
    p = init_portfolio()
    pos = p["positions"].get(symbol)
    if not pos or pos["shares"] < shares_to_sell:
        return {"success": False, "error": f"Posición insuficiente en {symbol}"}
    
    quote = fetch_quote(symbol)
    price = quote.get("price", 0.0)
    if price <= 0:
        return {"success": False, "error": f"No se pudo obtener precio en tiempo real para {symbol}"}
    
    revenue = shares_to_sell * price
    p["cash_usd"] += revenue
    
    pos["shares"] -= shares_to_sell
    if pos["shares"] <= 0.000001:
        del p["positions"][symbol]
    else:
        pos["total_invested"] = pos["shares"] * pos["avg_price"]
        p["positions"][symbol] = pos
        
    trade = {
        "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
        "type": "SELL",
        "symbol": symbol,
        "shares": round(shares_to_sell, 6),
        "price": price,
        "amount_usd": revenue
    }
    p["trade_history"].append(trade)
    save_portfolio(p)
    log(f"Paper Sell: Vendido {shares_to_sell:.4f} de {symbol} a ${price:,.2f} (+${revenue:.2f} USD)")
    return {"success": True, "trade": trade, "portfolio": p}

def get_portfolio_summary() -> Dict[str, Any]:
    p = init_portfolio()
    total_market_val = 0.0
    detailed_positions = []
    
    for symbol, pos in p.get("positions", {}).items():
        q = fetch_quote(symbol)
        curr_price = q.get("price", pos.get("avg_price", 0.0))
        mkt_val = pos["shares"] * curr_price
        pnl = mkt_val - pos["total_invested"]
        pnl_pct = (pnl / pos["total_invested"] * 100.0) if pos["total_invested"] > 0 else 0.0
        
        total_market_val += mkt_val
        detailed_positions.append({
            "symbol": symbol,
            "shares": round(pos["shares"], 6),
            "avg_price": round(pos["avg_price"], 2),
            "current_price": curr_price,
            "market_value": round(mkt_val, 2),
            "pnl": round(pnl, 2),
            "pnl_pct": round(pnl_pct, 2)
        })
        
    total_net_worth = p["cash_usd"] + total_market_val
    initial = p.get("initial_cash", 1000.0)
    total_profit = total_net_worth - initial
    total_profit_pct = (total_profit / initial * 100.0) if initial > 0 else 0.0
    
    return {
        "cash_usd": round(p["cash_usd"], 2),
        "market_value": round(total_market_val, 2),
        "net_worth": round(total_net_worth, 2),
        "total_profit": round(total_profit, 2),
        "total_profit_pct": round(total_profit_pct, 2),
        "positions": detailed_positions,
        "trades_count": len(p.get("trade_history", [])),
        "disclaimer": p.get("disclaimer")
    }

if __name__ == "__main__":
    if "--summary" in sys.argv:
        quotes = fetch_all_quotes()
        for q in quotes:
            sign = "+" if q.get("change_pct", 0) >= 0 else ""
            print(f"{q['name']} ({q['symbol']}): ${q.get('price', 0):,.2f} ({sign}{q.get('change_pct', 0)}%)")
    elif "--check-alerts" in sys.argv:
        check_price_alerts()
        print("Alertas verificadas.")
    elif "--portfolio" in sys.argv:
        print(json.dumps(get_portfolio_summary(), indent=2))
    else:
        print("Uso: kdf_market_radar.py {--summary | --check-alerts | --portfolio}")
