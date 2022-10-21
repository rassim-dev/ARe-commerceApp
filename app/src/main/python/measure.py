import cv2
import utlis

#IMPORTANT NOTES/PREREQUISITES/ASSUMPTIONS FOR TAKEING FOOT IMAGE
# 1- bright and solid (one-colored) background, preferably a floor
# 2- background color is not white
# 3- Wear black sock
# 4- A4 paper is visible
# 5- Foot touches the bottom edges of the paper
# 6- A4 paper is well-flatted after foot placement
# 7- Camera position not too far (paper blends with BG)
# nor too close(cant distinguish between paper and BG) :|
# 8- show a bit of the ankle/leg (bottom part)
# 9- the captured image must be in vertical orientation (foot facing forward/backward) "landscape mode is NOT allowed"
#  THE BOTTOM PART OF THE IMAGE (paper&foot) IS THE MOST IMPORTANT PART



def getFootLength(A4_height,foot_height_pix,A4_height_pix):
    """
    This method calculates the real/actual foot length

    Args:
        A4_height (int): Actual height of A4 in real life & measurement unit is (mm)
        foot_height_pix (int): height of foot's rectangle from img
        A4_height_pix (int): height of A4's rectangle from img (Reference Object)

    Returns:
        double: foot length rounded to the nearest one decimal place (XX.X)
    """
    foot_length = (A4_height * foot_height_pix) / A4_height_pix
    foot_length = foot_length / 10 #cm
    # print(foot_length)
    foot_length = round(foot_length,1)
    return foot_length

def main(img_path):
    ###############READING THE IMAGE############################
    # img_path = "good.jpg"

    img = cv2.imread(img_path)
    img_copy = img.copy()
    ###############PREPROCESS & FIND THE A4####################
    img_Thr = utlis.preprocess_paper(img_copy)
    contours,y,h = utlis.paperDetection(img_Thr,img)
    ###########################################################

    ##############HEIGTH/LENGTH OF A4 FROM IMAGE&REAL LIFE###############
    A4_height_pix = (y+h) - y
    A4_height = 297 #mm
    # largest_areas = sorted(contours, key=cv2.contourArea) #from smallest to largest
    # cv2.drawContours(img, [largest_areas[-2]], -1, 255, 5) #detected A4 paper area
    # showImage(img)
    ###############PREPROCESS & FIND THE FOOT####################
    img_dil = utlis.preprocess_foot(img_copy)
    y,h = utlis.footDetection(img_dil,img)
    ###########################################################


    ##############HEIGTH/LENGTH OF FOOT FROM IMAGE#############
    foot_height_pix = (y+h) - y
    # print("foot_height_pix",foot_height_pix)
    ##############ESTIMATED HEIGTH/LENGTH OF FOOT IN REAL LIFE###############
    foot_length = getFootLength(A4_height,foot_height_pix,A4_height_pix)
    return foot_length
    # print(str(foot_length)+" cm")
    #########################################################################
    # utlis.showImage(img,window_name="Result")

# main("good.jpg")