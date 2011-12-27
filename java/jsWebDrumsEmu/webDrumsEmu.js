/**
 * webDrumsEmu.js
 * (c) 2011, Immanuel Albrecht
 * 
 * This file is part of webDrumsEmu.
 *   
 *   webDrumsEmu is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   webDrumsEmu is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with webDrumsEmu.  If not, see <http://www.gnu.org/licenses/>.
 */


function addApplet(name, file, cname) {
	document.write("<applet name=\""+name+
			"\" archive=\""+file+
			"\" code=\""+cname+
			"\"  MAYSCRIPT width=\"800\" height=\"560\" > </applet>");
}


document.write("<p align=center>");
addApplet("drumsEmu","drumsEmu.jar","de.zorgk.drums.EmuApplet");
document.write("</p>");


document.write("<p>");
document.write("<small><small> This software is licensed under <a href=\"http://www.gnu.org/licenses/gpl.html\">GPLv3</a>.</small></small>");
document.write("</p>");

