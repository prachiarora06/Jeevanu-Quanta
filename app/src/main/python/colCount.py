import io
import cv2
import numpy as np
from scipy import ndimage
from skimage import feature, measure, color
from skimage.segmentation import watershed
from skimage.measure import regionprops
from skimage.morphology import remove_small_objects
from PIL import Image

class Rect:
    def __init__(self, x, y, w, h):
        self.x = int(x)
        self.y = int(y)
        self.w = int(w)
        self.h = int(h)

def colCount(inputImage, canvasRect, cropRect, thresholdValue, areaThreshold):
    img = cv2.imdecode(np.asarray(inputImage), cv2.IMREAD_COLOR)
    height = img.shape[0]
    width = img.shape[1]

    scale =  (width/2) / canvasRect[2]
    cropRect = Rect(
        cropRect[0] * scale,
        cropRect[1] * scale,
        cropRect[2] * scale,
        cropRect[3] * scale
    )
    mask = np.zeros(img.shape[:2], dtype=np.uint8)
    cv2.ellipse(
        img = mask,
        center = (cropRect.x, cropRect.y),
        axes = (cropRect.w, cropRect.h),
        angle = 0,
        startAngle = 0,
        endAngle = 360,
        color = (255, 255, 255),
        thickness = -1
    )
    img = cv2.bitwise_and(img, img, mask=mask)

    image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    thresh, output_binthresh = cv2.threshold(image, thresholdValue, 254, cv2.THRESH_BINARY)
    kernel = np.ones((3),np.uint8)
    clear_image = cv2.morphologyEx(output_binthresh,cv2.MORPH_OPEN, kernel, iterations=3)
    label_image = clear_image.copy()
    label_count = 0
    rows, cols = label_image.shape
    for j in range(rows):
        for i in range(cols):
            pixel = label_image[j, i]
            if 255 == pixel:
                label_count += 1
                cv2.floodFill(label_image, None, (i, j), label_count)

    contours, hierarchy = cv2.findContours(clear_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    output_contour = cv2.cvtColor(clear_image, cv2.COLOR_GRAY2BGR)
    cv2.drawContours(output_contour, contours, -1, (0, 0, 255), 2)
    dist_trans = ndimage.distance_transform_edt(clear_image)
    local_max = feature.peak_local_max(dist_trans, min_distance=2)
    local_max_mask = np.zeros(dist_trans.shape, dtype=bool)
    local_max_mask[tuple(local_max.T)] = True
    labels = watershed(-dist_trans, measure.label(local_max_mask), mask=clear_image)
    labels = remove_small_objects(labels, areaThreshold)
    result = color.label2rgb(labels, bg_label=0)
    props = regionprops(labels)
    props = [p for p in props if p['area']>areaThreshold]
    #checking in which quad does the seg lies in
    quadCount = []
    quadCount.append(len([p for p in props if p['centroid'][0]<=width/2 and p['centroid'][1]>height/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]<=width/2 and p['centroid'][1]<=height/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]>width/2 and p['centroid'][1]<=height/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]>width/2 and p['centroid'][1]>height/2]))

    f = io.BytesIO()
    im = Image.fromarray((result * 255).astype(np.uint8))
    im.save(f, "png")
    return f.getvalue(), len(props), quadCount
