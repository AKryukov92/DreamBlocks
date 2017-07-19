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

package com.lordstephen77.dreamblocks.ui;

import java.util.Optional;

import com.lordstephen77.dreamblocks.GraphicsHandler;
import com.lordstephen77.dreamblocks.MainGame;
import com.lordstephen77.dreamblocks.Sprite;
import com.lordstephen77.dreamblocks.SpriteStore;

/**
 * <p>World selection screen (second screen)</p>
 * @author Александр, Stefano Peris
 * @version 0.3
 */
public class NewGameMenu {

    /* menu sprites */
    private final Button menu_mini;
    private final Button menu_medium;
    private final Button menu_big;

    private final Sprite menu_bgTile;
    private final Sprite menu_logo;
    private final Sprite menu_tag;

    private MainGame game;

    public NewGameMenu(MainGame g, SpriteStore ss) {
        this.game = g;
        menu_bgTile = ss.getSprite("sprites/tiles/wood.png");
        menu_logo = ss.getSprite("sprites/menus/title.png");
        menu_tag = ss.getSprite("sprites/menus/tag.png");
        menu_mini = new Button(200, 160, 64, ss.getSprite("sprites/menus/mini_up.png"), ss.getSprite("sprites/menus/mini_down.png"));
        menu_medium = new Button(300, 160, 64, ss.getSprite("sprites/menus/med_up.png"), ss.getSprite("sprites/menus/med_down.png"));
        menu_big = new Button(400, 160, 64, ss.getSprite("sprites/menus/big_up.png"), ss.getSprite("sprites/menus/big_down.png"));
    }

    public void resize(int screenWidth, int screenHeight){
        menu_mini.resize(screenWidth, screenHeight);
        menu_medium.resize(screenWidth, screenHeight);
        menu_big.resize(screenWidth, screenHeight);
    }

    // menu title + animated logo
    public void draw(GraphicsHandler g) {
        game.drawTileBackground(g, menu_bgTile, 60);
        game.drawCenteredX(g, menu_logo, 70, 397, 50);
        float tagScale = ((float) Math.abs((game.ticksRunning % 100) - 50)) / 50 + 1;
        g.drawImage(menu_tag, 610, 60, (int) (60 * tagScale), (int) (37 * tagScale));

        int mouseX = game.screenMousePos.x;
        int mouseY = game.screenMousePos.y;
        if (menu_mini.isInside(mouseX, mouseY)) {
            g.drawImage(menu_mini.getSpriteHover(), menu_mini);
        } else {
            g.drawImage(menu_mini.getSpriteDefault(), menu_mini);
        }
        if (menu_medium.isInside(mouseX, mouseY)){
            g.drawImage(menu_medium.getSpriteHover(), menu_medium);
        } else {
            g.drawImage(menu_medium.getSpriteDefault(), menu_medium);
        }
        if (menu_big.isInside(mouseX, mouseY)){
            g.drawImage(menu_big.getSpriteHover(), menu_big);
        } else {
            g.drawImage(menu_big.getSpriteDefault(), menu_big);
        }
    }

    public Optional<WORLD_WIDTH> handleClick(int x, int y){
        if(menu_mini.isInside(x, y)){
            return Optional.of(WORLD_WIDTH.MINI);
        } else if (menu_medium.isInside(x, y)){
            return Optional.of(WORLD_WIDTH.MEDIUM);
        } else if (menu_big.isInside(x, y)){
            return Optional.of(WORLD_WIDTH.BIG);
        } else {
            return Optional.empty();
        }
    }
}
