/*
 _(`-')      (`-')  (`-')  _(`-')  _ <-. (`-')  <-.(`-')                               <-.(`-')  (`-').->
( (OO ).-><-.(OO )  ( OO).-/(OO ).-/    \(OO )_  __( OO)    <-.        .->    _         __( OO)  ( OO)_
 \    .'_ ,------,)(,------./ ,---.  ,--./  ,-.)'-'---.\  ,--. )  (`-')----.  \-,-----.'-'. ,--.(_)--\_)
 '`'-..__)|   /`. ' |  .---'| \ /`.\ |   `.'   || .-. (/  |  (`-')( OO).-.  '  |  .--./|  .'   //    _ /
 |  |  ' ||  |_.' |(|  '--. '-'|_.' ||  |'.'|  || '-' `.) |  |OO )( _) | |  | /_) (`-')|      /)\_..`--.
 |  |  / :|  .   .' |  .--'(|  .-.  ||  |   |  || /`'.  |(|  '__ | \|  |)|  | ||  |OO )|  .   ' .-._)   \
 |  '-'  /|  |\  \  |  `---.|  | |  ||  |   |  || '--'  / |     |'  '  '-'  '(_'  '--'\|  |\   \\       /
 `------' `--' '--' `------'`--' `--'`--'   `--'`------'  `-----'    `-----'    `-----'`--' '--' `-----'
                                     _                    _ ___
                                    |_) _    _ ._  _|  __|_  |o._ _  _
                                    |_)(/_\/(_)| |(_| (_)|   ||| | |(/_
                                          /


Copyright (C) 2017 Stefano Peris

nickname: LordStephen77
eMail: lordstephen77@gmail.com
Github: https://github.com/DreamBlocks

is free software: you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.lordstephen77.dreamblocks;

import com.lordstephen77.dreamblocks.ui.CraftingGrid;

import java.util.Optional;

public class Inventory implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
    private int tileSize = 16;
    private int seperation = 15;
	
	public InventoryItem[][] inventoryItems;
    private InventoryItem[] hotbarRow;
    private int panelWidth;
    private int panelHeight;

    private int maxCount = 64;
	private int playerRow;
	private InventoryItem holding = new InventoryItem(null);
    private CraftingGrid craftingGrid;
    private Int2 clickPos = new Int2(0, 0);
	public int craftingHeight;
	private InventoryItem craftable = new InventoryItem(null);
	
	public Inventory(int width, int height, int craftingHeight) {
		inventoryItems = new InventoryItem[width][height + craftingHeight];
        panelWidth = inventoryItems.length * (tileSize + seperation) + seperation;
        panelHeight = inventoryItems[0].length * (tileSize + seperation) + seperation;
        hotbarRow = new InventoryItem[width];
        playerRow = height + craftingHeight - 1;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height + craftingHeight; j++) {
				inventoryItems[i][j] = new InventoryItem(null);
			}
			hotbarRow[i] = inventoryItems[i][playerRow];
		}
		craftingGrid = new CraftingGrid(tileSize, seperation);
        craftingGrid.setInventoryItems(inventoryItems);

		this.craftingHeight = craftingHeight;
	}
	
	public void addItem(Item item, int count) {
		// try active slots
		int itemsToGo = inventoryItems[0][playerRow].add(item, count);
		for (int i = 0; i < inventoryItems.length && itemsToGo > 0; i++) {
			itemsToGo = inventoryItems[i][playerRow].add(item, count);
		}
		
		// try the rest
		for (int i = 0; i < inventoryItems.length && itemsToGo > 0; i++) {
			for (int j = 0; j < inventoryItems[0].length - 1 && itemsToGo > 0; j++) {
				if ((j < craftingHeight && i < inventoryItems.length - craftingGrid.getTableSizeAvailable())
						|| (craftingHeight != craftingGrid.getTableSizeAvailable() && j == craftingGrid.getTableSizeAvailable())) {
					continue;
				}
				itemsToGo = inventoryItems[i][playerRow].add(item, count);
			}
		}
	}

	// returns true if the mouse hit in the inventory
	public boolean updateInventory(int screenWidth, int screenHeight,
			Int2 mousePos, boolean leftClick, boolean rightClick) {
        updateCraftingResult();
		int panelWidth = inventoryItems.length * (tileSize + seperation) + seperation;
		int panelHeight = inventoryItems[0].length * (tileSize + seperation) + seperation;
		int x = screenWidth / 2 - panelWidth / 2;
		int y = screenHeight / 2 - panelHeight / 2;

		if (!isMouseInsideInventory(mousePos, x, y, panelWidth, panelHeight)) {
			return false;
		}

		if (leftClick || rightClick) {
            Int2 position = mouseToCoor(mousePos.x - x, mousePos.y - y, seperation, tileSize);
            if (position != null) {
                InventoryItem itemUnderCursor = inventoryItems[position.x][position.y];
                if (holding.isEmpty()) {
                    if (rightClick && itemUnderCursor.count > 1) {
                        pickHalfOfStack(itemUnderCursor, holding);
                    } else {
                        pickWholeStack(itemUnderCursor, holding);
                    }
                } else if (itemUnderCursor.item == null) {
                    if (rightClick) {
                        dropSingleItemToEmptyTile(itemUnderCursor, holding);
                    } else {
                        dropWholeStackToEmptyTile(itemUnderCursor, holding);
                    }
                } else if (holding.item.item_id == itemUnderCursor.item.item_id
                        && itemUnderCursor.count < maxCount) {
                    if ((holding.item.getClass() == Tool.class)
                            || (itemUnderCursor.item.getClass() == Tool.class)) {
                    } else if (rightClick) {
                        dropSingleItemToStack(itemUnderCursor, holding);
                    } else {
                        dropStackToStack(itemUnderCursor, holding);
                    }
                } else {
                    swapItems(itemUnderCursor, holding);
                }
            }

            if (isMouseOverCraftingResult(screenWidth, screenHeight, panelWidth, panelHeight, mousePos)){
                craftItem();
            }
        }
        return true;
	}

    private void pickHalfOfStack(InventoryItem grid, InventoryItem hand){
        hand.item = grid.item;
        hand.count = (int) Math.ceil((double) grid.count / 2);
        grid.count = (int) Math.floor((double) grid.count / 2);
    }

    private void pickWholeStack(InventoryItem grid, InventoryItem hand){
        hand.item = grid.item;
        hand.count = grid.count;
        grid.item = null;
        grid.count = 0;
    }

    private void dropSingleItemToEmptyTile(InventoryItem grid, InventoryItem hand){
        grid.item = hand.item;
        grid.count = 1;
        hand.count--;
        if (hand.count <= 0) {
            hand.item = null;
        }
    }

    private void dropWholeStackToEmptyTile(InventoryItem grid, InventoryItem hand){
        grid.item = hand.item;
        grid.count = hand.count;
        hand.item = null;
        hand.count = 0;
    }

    private void dropSingleItemToStack(InventoryItem grid, InventoryItem hand){
        grid.count++;
        hand.count--;
        if (hand.count <= 0) {
            hand.item = null;
        }
    }

    private void dropStackToStack(InventoryItem grid, InventoryItem hand){
        grid.count += hand.count;
        if (grid.count > maxCount) {
            hand.count = maxCount - grid.count;
            grid.count = maxCount;
        } else {
            hand.item = null;
            hand.count = 0;
        }
    }

    private void swapItems(InventoryItem grid, InventoryItem hand){
        Item item = grid.item;
        int count = grid.count;
        grid.item = hand.item;
        grid.count = hand.count;
        hand.item = item;
        hand.count = count;
    }

    private boolean isMouseInsideInventory(Int2 mousePos, int x, int y, int panelWidth, int panelHeight){
        return x <= mousePos.x && mousePos.x <= x + panelWidth
                && y <= mousePos.y && mousePos.y <= y + panelHeight;
    }

	private boolean isMouseOverCraftingResult(int screenWidth, int screenHeight, int panelWidth, int panelHeight, Int2 mousePos){
	    int x, y;
        x = screenWidth / 2 - panelWidth / 2;
        y = screenHeight / 2 - panelHeight / 2;
        x = x + (inventoryItems.length - craftingGrid.getTableSizeAvailable() - 1) * (tileSize + seperation) - 5;
        y = y + seperation * 2 + tileSize - 5;
	    return mousePos.x >= x && mousePos.x <= x + tileSize + 10 && mousePos.y >= y
                && mousePos.y <= y + tileSize * 2 + 10;
    }

    private void takeRecipeMaterials(){
        for (int i = 0; i < craftingGrid.getTableSizeAvailable(); i++) {
            for (int j = 0; j < craftingGrid.getTableSizeAvailable(); j++) {
                int actualI = i + inventoryItems.length - craftingGrid.getTableSizeAvailable();
                inventoryItems[actualI][j].count -= 1;
                if (inventoryItems[actualI][j].count <= 0) {
                    inventoryItems[actualI][j].item = null;
                    inventoryItems[actualI][j].count = 0;
                }
            }
        }
    }

    private void craftItem(){
        Optional<Item> recipeResult = craftingGrid.getResult();
        if (recipeResult.isPresent()) {
            if (recipeResult.get().getClass() != Tool.class || holding.isEmpty()) {
                takeRecipeMaterials();
                int count = recipeResult.get().template.outCount;
                holding.add(recipeResult.get().clone(), count);
            }
        }
    }

	private void updateCraftingResult(){
		Optional<Item> nextResult = craftingGrid.getResult();
        if (nextResult.isPresent()){
            craftable.item = nextResult.get();
            craftable.count = nextResult.get().template.outCount;
        } else {
            craftable.item = null;
            craftable.count = 0;
        }
	}

    // relative x/y in px
    private Int2 mouseToCoor(int x, int y, int seperation, int tileSize) {
        clickPos.x = x / (seperation + tileSize);
        clickPos.y = y / (seperation + tileSize) - 1;
        if (clickPos.x < 0
                || clickPos.y < 0
                || clickPos.x >= inventoryItems.length
                || clickPos.y >= inventoryItems[0].length
                || ((clickPos.y < craftingHeight && clickPos.x < inventoryItems.length - craftingGrid.getTableSizeAvailable())
                || (craftingHeight != craftingGrid.getTableSizeAvailable() && clickPos.y == craftingGrid.getTableSizeAvailable()))) {
            return null;
        }
        return clickPos;
    }

    public InventoryItem[] getHotbarRow(){
	    return hotbarRow;
    }

    private void drawPanel(GraphicsHandler g, int screenWidth, int screenHeight, int panelWidth, int panelHeight){
        int x = screenWidth / 2 - panelWidth / 2;
        int y = screenHeight / 2 - panelHeight / 2;
        g.setColor(Color.gray);
        g.fillRect(x, y, panelWidth, panelHeight);
    }

    private void drawInventoryCell(GraphicsHandler g, int x, int y, InventoryItem item){
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x + seperation - 2, y + seperation - 2, tileSize + 4, tileSize + 4);
        item.draw(g, x + seperation, y + seperation, tileSize);
    }

    private void drawCraftingResult(GraphicsHandler g, int screenWidth, int screenHeight, int panelWidth, int panelHeight){
        g.setColor(Color.orange);
        int x = screenWidth / 2 - panelWidth / 2 + (inventoryItems.length - craftingGrid.getTableSizeAvailable() - 1) * (tileSize + seperation);
        int y = screenHeight / 2 - panelHeight / 2 + seperation * 2 + tileSize;
        g.fillRect(x - 5, y - 5, tileSize + 10, tileSize + 10);
        craftable.draw(g, x, y, tileSize);
    }

    private void drawBackpack(GraphicsHandler g, int screenWidth, int screenHeight, int panelWidth, int panelHeight){
        int x;
        int y = screenHeight / 2 - panelHeight / 2 + (tileSize + seperation) * 3;
        for (int rowIdx = 3; rowIdx < inventoryItems[0].length; rowIdx++){
            x = screenWidth / 2 - panelWidth / 2;
            for (int colIdx = 0; colIdx < inventoryItems.length; colIdx++){
                drawInventoryCell(g, x, y, inventoryItems[colIdx][rowIdx]);
                x += tileSize + seperation;
            }
            y += tileSize + seperation;
        }
    }

    public void draw(GraphicsHandler g, int screenWidth, int screenHeight, Int2 mousePos) {
        drawPanel(g, screenWidth, screenHeight, panelWidth, panelHeight);
        craftingGrid.draw(g, screenWidth, screenHeight, panelWidth, panelHeight);
        drawBackpack(g, screenWidth, screenHeight, panelWidth, panelHeight);
        drawCraftingResult(g, screenWidth, screenHeight, panelWidth, panelHeight);

        int holdingX = mousePos.x - tileSize / 2;
        int holdingY = mousePos.y - tileSize - tileSize / 2;
        holding.draw(g, holdingX, holdingY, tileSize);
    }

    public void setTableSizeAvailable(int tableSizeAvailable) {
        this.craftingGrid.setTableSizeAvailable(tableSizeAvailable);
    }
}
