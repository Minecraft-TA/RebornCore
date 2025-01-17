/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package reborncore.client.gui.builder.slot.elements;

import java.util.ArrayList;
import java.util.List;

public class SpriteContainer {
	public List<OffsetSprite> offsetSprites = new ArrayList<>();

	public SpriteContainer setSprite(int index, OffsetSprite sprite) {
		offsetSprites.set(index, sprite);
		return this;
	}

	public SpriteContainer setSprite(int index, ISprite sprite, int offsetX, int offsetY) {
		if (sprite instanceof Sprite) {
			offsetSprites.set(index, new OffsetSprite(sprite).setOffsetX(((Sprite) sprite).offsetX + offsetX).setOffsetY(((Sprite) sprite).offsetY + offsetY));
		} else {
			offsetSprites.set(index, new OffsetSprite(sprite, offsetX, offsetY));
		}
		return this;
	}

	public SpriteContainer setSprite(int index, ISprite sprite) {
		if (sprite instanceof Sprite) {
			offsetSprites.set(index, new OffsetSprite(sprite).setOffsetX(((Sprite) sprite).offsetX).setOffsetY(((Sprite) sprite).offsetY));
		} else {
			offsetSprites.add(index, new OffsetSprite(sprite));
		}
		return this;
	}

	public SpriteContainer addSprite(OffsetSprite sprite) {
		offsetSprites.add(sprite);
		return this;
	}

	public SpriteContainer addSprite(ISprite sprite, int offsetX, int offsetY) {
		if (sprite instanceof Sprite) {
			offsetSprites.add(new OffsetSprite(sprite).setOffsetX(((Sprite) sprite).offsetX + offsetX).setOffsetY(((Sprite) sprite).offsetY + offsetY));
		} else {
			offsetSprites.add(new OffsetSprite(sprite, offsetX, offsetY));
		}
		return this;
	}

	public SpriteContainer addSprite(ISprite sprite) {
		if (sprite instanceof Sprite) {
			offsetSprites.add(new OffsetSprite(sprite).setOffsetX(((Sprite) sprite).offsetX).setOffsetY(((Sprite) sprite).offsetY));
		} else {
			offsetSprites.add(new OffsetSprite(sprite));
		}
		return this;
	}
}
