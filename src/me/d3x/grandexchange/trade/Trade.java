package me.d3x.grandexchange.trade;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Trade {
	
	private String sellerUID;
	private String itemName;
	private int quantity;
	private int originalQuantity;
	private double price;
    private double unitPrice;
	private int type;
	private UUID uuid;
    
	public Trade(String sellerUID, String itemName, int quantity, double unitPrice, int type) {
		this.sellerUID = sellerUID;
		this.itemName = itemName;
		this.quantity = quantity;
		this.originalQuantity = quantity;
		this.price = unitPrice * quantity;
		this.unitPrice = unitPrice;
		this.type = type;
        this.uuid = UUID.fromString(sellerUID);
	}

	public String getSellerUID() {
		return sellerUID;
	}
	
	public String getItemName() {
	    return this.itemName;
	}
    
    public UUID getUUID(){
        return uuid;
    }

	public int getQuantity() {
		return quantity;
	}
	
	public int getOriginalQuantity() {
	    return originalQuantity;
	}

    public void reduceQuantity(Trade other, String itemName){
        this.quantity -= (other.getQuantity() <= 0) ? other.getOriginalQuantity() : other.getQuantity();
        this.quantity = Math.max(0, this.quantity);
        int delta = this.originalQuantity - (this.quantity > 0 ? this.quantity : 0);
        this.price = this.quantity * this.getUnitPrice();
        if(this.getType() == 0) {
            ItemStack[] items = new ItemStack[delta / 64 + 1];
            int itemsLeft = delta;
            for(int i = 0; i < items.length; i++) {
                items[i] = itemsLeft > 64 ? new ItemStack(Material.getMaterial(itemName), 64) : new ItemStack(Material.getMaterial(itemName), itemsLeft);
                itemsLeft -= 64;
            }
            double offset = this.getUnitPrice() - other.getUnitPrice();
            double excess = offset * delta;
            //double buyPrice = (this.getUnitPrice() * delta) - ((excess > 0) ? excess : 0);
            TradeManager.getInstance().addNewCollectableTrade(this.sellerUID, itemName, items, excess);
        }else {
            TradeManager.getInstance().addNewCollectableTrade(this.sellerUID, itemName, null, this.getUnitPrice() * delta);
        }
        if(this.quantity > 0) {
            this.originalQuantity = this.quantity;
        }
    }
    
    public void cancel() {
        int delta = this.quantity;
        if(this.getType() == 1) {
            ItemStack[] items = new ItemStack[delta / 64 + 1];
            int itemsLeft = delta;
            for(int i = 0; i < items.length; i++) {
                items[i] = itemsLeft > 64 ? new ItemStack(Material.getMaterial(itemName), 64) : new ItemStack(Material.getMaterial(itemName), itemsLeft);
                itemsLeft -= 64;
            }
            //double offset = this.getUnitPrice();
            //double excess = offset * delta;
            //double buyPrice = (this.getUnitPrice() * delta) - ((excess > 0) ? excess : 0);
            TradeManager.getInstance().addNewCollectableTrade(this.sellerUID, itemName, items, 0, true);
        }else {
            TradeManager.getInstance().addNewCollectableTrade(this.sellerUID, itemName, null, this.getUnitPrice() * delta, true);
        }
        
    }

	public double getPrice() {
		return price;
	}
	
	/**
	 * @return 0 if buying, 1 if selling
	 */
	public int getType() {
		return this.type;
	}
	
	public double getUnitPrice() {
		return this.unitPrice;
	}
    
    public int compareTo(Trade other){
        return (getType() == 0) ? -(int)(this.getUnitPrice() - other.getUnitPrice()) : (int)(this.getUnitPrice() - other.getUnitPrice());
    }
    
	/**
	 * @param other - trade object that isn't on the marketplace yet
	 */
    public boolean canCompleteTrade(Trade other){
        return this.getType() != other.getType() && (this.getType() == 0 ? this.compareTo(other) >= 0 : this.compareTo(other) <= 0);
    }
    
    public String getText() {
        return (getType()== 0 ? "[\2472BUY\247a]" : "[SELL]") + " [\2472" + getQuantity() + "\247a] [\2472" + getItemName() + "\247a] for [\2472" + getPrice() + "\247a] gp";
    }

    
}
