/*
    oopp-project-56
    Copyright (C) 2021  CSE1105 - OOP Project / 2020-2021 / Team Repositories / oopp-group-56
    Giulio Segalini, Jegor Zelenjak, Codrin Socol, Tudor Popica, Oleg Danilov, Artjom Pugatsov
    TUDelft

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, exclusively version 3 of the License
    or GNU Affero General Public License version 3.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package nl.tudelft.oopp.livechat;

import nl.tudelft.oopp.livechat.views.MainSceneDisplay;


/**
 * Launches the client-side part of the program.
 */
public class MainApp {
    public static void main(String[] args) {
        System.setProperty("prism.allowhidpi", "false");
        MainSceneDisplay.main(new String[0]);
    }
}
