/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Jamalam
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.github.jamalam360.reaping.content.compat;

import io.github.jamalam360.reaping.ReapingInit;
import io.github.jamalam360.reaping.ReapingLogic;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;

@SuppressWarnings({"unused", "deprecation"})
public class HarvestScythes implements ModInitializer {

    @Override
    public void onInitialize() {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Item> clazz = (Class<? extends Item>) ReapingInit.class.getClassLoader()
                  .loadClass("wraith.harvest_scythes.item.ScytheItem");
            ReapingLogic.addReapingTool(clazz);
        } catch (Exception e) {
            ReapingInit.LOGGER.warn("Failed to enable Harvest Scythe compatibility");
        }
    }
}
