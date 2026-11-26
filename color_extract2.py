import sys
from PIL import Image
import collections

img = Image.open('app/src/main/res/drawable/app_logo.webp').convert('RGB')
pixels = list(img.getdata())
# filter out white and black to get actual brand colors
filtered_pixels = [p for p in pixels if not (p[0] > 240 and p[1] > 240 and p[2] > 240) and not (p[0] < 15 and p[1] < 15 and p[2] < 15)]

# bucket colors by reducing resolution
bucketed = [(r//16 * 16, g//16 * 16, b//16 * 16) for r, g, b in filtered_pixels]
counter = collections.Counter(bucketed)
print("COLORS:")
for color, count in counter.most_common(10):
    print(f'#{color[0]:02x}{color[1]:02x}{color[2]:02x}')
