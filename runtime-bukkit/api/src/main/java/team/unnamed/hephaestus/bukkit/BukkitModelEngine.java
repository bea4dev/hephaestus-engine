/*
 * This file is part of hephaestus-engine, licensed under the MIT license
 *
 * Copyright (c) 2021-2023 Unnamed Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package team.unnamed.hephaestus.bukkit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.ModelEngine;

/**
 * The hephaestus model engine abstraction for
 * Bukkit-based server implementations such as
 * Spigot and Paper
 *
 * @since 1.0.0
 */
public interface BukkitModelEngine extends ModelEngine<Player, Location> {

    ModelEntity createViewAndTrack(Model model, Location location, CreatureSpawnEvent.SpawnReason reason);

    @Override
    default ModelEntity createViewAndTrack(Model model, Location location) {
        return createViewAndTrack(model, location, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    ModelEntity createView(Model model, Location location);

}