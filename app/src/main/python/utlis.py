import cv2
import numpy as np

def showImage(img,window_name):
    """
    This method resizes and shows an img in 540x540 pixels
    """
    img = cv2.resize(img,(540,540))
    cv2.imshow(window_name,img)
    cv2.waitKey(0)

def preprocess_paper(img):
    #gray version of the image
    img_gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    #canny edged detection image
    img_can = cv2.Canny(img_gray,20,40)

    kernel = np.ones((5,5),np.uint8)
    #better edges image (optimization)
    img_dil = cv2.dilate(img_can,kernel,iterations = 1)
    img_Thr = cv2.erode(img_dil,kernel,iterations = 1)
    return img_Thr

def paperDetection(img_Thr,img):
    #better edges image (optimization)
    _,thresh = cv2.threshold(img_Thr, 200, 255, 0)
    #find similar areas in the image (thresh)
    contours, _ = cv2.findContours(thresh,  
    cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE) 

    #sorting areas based on size
    contours = sorted(contours, key=cv2.contourArea) 

    #obtaining the corners/pts of the detected A4 (rectangle)
    x,y,w,h = cv2.boundingRect(contours[-2]) #second largest_areas (A4)
    # a4_paper_height = h
    cv2.rectangle(img,(x,y),(x+w,y+h),(0,0,255),5) #draw the detected rectangle
    # cv2.circle(img,(x,y),2,(0,255,0),9) # Top left point
    cv2.circle(img,(x+w,y+h),2,(0,255,0),12) # bottom left point
    cv2.circle(img,(x+w,y),2,(0,255,0),12) # top right point
    #return rectangle (A4) height and contours (visualization purposes only)
    return contours,y,h

def preprocess_foot(img):
    #HSV version of the image
    hsv_img = cv2.cvtColor(img,cv2.COLOR_BGR2HSV)
    h,s,v = cv2.split(hsv_img) # == hsv_img[2]

    #finding image threshold based on the 'v' dimension
    _,img_thr = cv2.threshold(v,87,255,cv2.THRESH_BINARY)

    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE,(3,3))
    #better foot edges image (optimization)
    #perform dilation on the inverted image since ROI was not highlighted
    img_dil = cv2.dilate(cv2.bitwise_not(img_thr),kernel,iterations=4)
    return img_dil

def footDetection(img_dil,img):
    #find similar areas in the foot image (thresh)
    conts, _ = cv2.findContours(img_dil,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

    big_con = 0 #biggest area == foot (black pixels)
    max_area = 0

    for c in conts:
        if cv2.contourArea(c) > max_area:
            max_area = cv2.contourArea(c)
            big_con = c

    #obtaining the corners/pts/coordinates of the detected foot "contour with max area" (rectangle)
    x,y,w,h = cv2.boundingRect(big_con)
    cv2.rectangle(img,(x,y),(x+w,y+h),(0,255,0),5) #draw the detected rectangle
    cv2.circle(img,(x+w,y+h),2,(255,0,0),12) # bottom left point
    cv2.circle(img,(x+w,y),2,(255,0,0),12) # top right point
    #return rectangle (Foot) height
    return y,h 





