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


package com.lgs.dreamblocks;

import com.lgs.dreamblocks.Constants.TileID;

public final class TileTemplate implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	public final static TileTemplate tree = new TileTemplate(
			new TileID[][] {
					{ TileID.NONE, TileID.LEAVES, TileID.LEAVES, TileID.NONE, TileID.NONE,
							TileID.NONE },
					{ TileID.LEAVES, TileID.LEAVES, TileID.LEAVES, TileID.LEAVES, TileID.NONE,
							TileID.NONE },
					{ TileID.LEAVES, TileID.LEAVES, TileID.LEAVES, TileID.WOOD, TileID.WOOD,
							TileID.WOOD },
					{ TileID.LEAVES, TileID.LEAVES, TileID.LEAVES, TileID.LEAVES, TileID.NONE,
							TileID.NONE },
					{ TileID.NONE, TileID.LEAVES, TileID.LEAVES, TileID.NONE, TileID.NONE,
							TileID.NONE } }, 5, 2);
	public TileID[][] template;
	public int spawnX;
	public int spawnY;
	
	private TileTemplate(TileID[][] tileIDs, int spawnX, int spawnY) {
		this.template = tileIDs;
		this.spawnX = spawnX;
		this.spawnY = spawnY;
	}
}