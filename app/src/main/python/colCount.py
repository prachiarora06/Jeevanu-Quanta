import io
import cv2
import numpy as np
from scipy import ndimage
from skimage import feature, measure, color
from skimage.segmentation import watershed
from skimage.measure import regionprops
from skimage.morphology import remove_small_objects
from PIL import Image

def colCount(inputImage, thresholdValue, areaThreshold):
    img = cv2.imdecode(np.asarray(inputImage), cv2.IMREAD_COLOR)
    sizey = img.shape[0]
    sizex = img.shape[1]
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
    quadCount.append(len([p for p in props if p['centroid'][0]<=sizex/2 and p['centroid'][1]>sizey/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]<=sizex/2 and p['centroid'][1]<=sizey/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]>sizex/2 and p['centroid'][1]<=sizey/2]))
    quadCount.append(len([p for p in props if p['centroid'][0]>sizex/2 and p['centroid'][1]>sizey/2]))

    f = io.BytesIO()
    im = Image.fromarray((result * 255).astype(np.uint8))
    im.save(f, "png")
    return f.getvalue(), len(props), quadCount
