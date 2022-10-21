package com.example.fitkit

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_measure.*
import java.lang.ref.WeakReference

/**
 *Disclaimer: These classes are taken from https://github.com/Terran-Marine/ARCoreMeasuredDistance,
 * but are modified by us to fit our needs :)
 */

class MeasureActivity : AppCompatActivity() {
    private val dataArray = arrayListOf<AnchorInfoBean>()
    private val lineNodeArray = arrayListOf<Node>()
    private val sphereNodeArray = arrayListOf<Node>()
    private val startNodeArray = arrayListOf<Node>()
    private val endNodeArray = arrayListOf<Node>()

    private lateinit var startNode : AnchorNode
    private var esti_area : Double = 0.0
    private var countTap = 0

    private var model: Renderable? = null
    private var modelSize : Float = 1000.0F //cm^2
    private lateinit var anchorNode3DObject : AnchorNode

    private var modelLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_measure)
        
        // Retrieve link to 3D model from product display page
        modelLink = intent.getStringExtra("modelLink")
        Log.d("AR_MEASURE", "Link: " + modelLink)
        
        // Retrieve estimated area of model from product display page
        modelSize = intent.getFloatExtra("area", 0.0F)
        Log.d("AR_MEASURE", "Model Size: " + modelSize)

        displayMsg("Please place 4 points to perform room measurement")
        loadModels()
        initView()
    }

    private fun initView(){

        //Undo button
        UI_Last.setOnClickListener{
            //Previous
            when(dataArray.size){

                0 -> {
                    countTap = 0
                    Toast.makeText(this,"no operation record", Toast.LENGTH_LONG)
                }
                1 -> {
                    countTap--
                    dataArray.clear()
                    lineNodeArray.clear()
                    sphereNodeArray.clear()
                    startNodeArray.clear()
                    endNodeArray.clear()
                    (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(startNode)
                }
                else -> {
                    if(countTap == 6){
                        countTap--
                        (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(anchorNode3DObject)

                    }else {
                        countTap--
                        dataArray.removeAt(dataArray.size - 1)
                        val index = startNodeArray.size - 1
                        startNodeArray[index].removeChild(lineNodeArray.removeAt(index))
                        endNodeArray[index].removeChild(sphereNodeArray.removeAt(index + 1))
                        (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(
                            startNodeArray.removeAt(index)
                        )
                        (UI_ArSceneView as MyArFragment).arSceneView.scene.removeChild(
                            endNodeArray.removeAt(
                                index
                            )
                        )
                    }
                }

            }
        }

        initAr()

    }

    private fun displayMsg(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    private fun initAr() {

        (UI_ArSceneView as MyArFragment).setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            countTap++
//            Log.d("count", "" + countTap)
            if(countTap < 4)
                Toast.makeText(this,"${(4-countTap)} point(s) left",Toast.LENGTH_SHORT).show()
            if(countTap == 4)
                displayMsg("Tap anywhere on the screen to finish the measurement")
            if(countTap == 5)
                displayMsg("Tap again to show the 3d object")

            if (countTap < 6) {


            if (dataArray.size == 4) {
                val endAnchor = dataArray[dataArray.size - 1].anchor
                val startAnchor = dataArray[0].anchor
                val startPose = endAnchor.pose
                val endPose = startAnchor.pose
                val dx = startPose.tx() - endPose.tx()
                val dy = startPose.ty() - endPose.ty()
                val dz = startPose.tz() - endPose.tz()

                val anchorInfoBean = AnchorInfoBean("", hitResult.createAnchor(), 0.0)
                dataArray.add(anchorInfoBean)
                //euclidean distance
                anchorInfoBean.length = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())
                val edge1L = dataArray[1].length * 100
                val edge2W = dataArray[2].length * 100
                val edge3L = dataArray[3].length * 100
                val edge4W = dataArray[4].length * 100

                val avg_length = (edge1L + edge3L) / 2
                val avg_width = (edge2W + edge4W) / 2

                esti_area = avg_length * avg_width //A = wl
//                  esti_area_tv.text = "${String.format("%.1f", esti_area * 100)}CM^2"
                esti_area_tv.text = "Estimated Area: ${String.format("%.1f", esti_area)} cm^2"

                drawLine(startAnchor, endAnchor, anchorInfoBean.length)


            }
            if (dataArray.size > 3) {
                return@setOnTapArPlaneListener
            }

            val anchorInfoBean = AnchorInfoBean("", hitResult.createAnchor(), 0.0)
            dataArray.add(anchorInfoBean)

            if (dataArray.size > 1) {
                val endAnchor = dataArray[dataArray.size - 1].anchor
                val startAnchor = dataArray[dataArray.size - 2].anchor


                val startPose = endAnchor.pose
                val endPose = startAnchor.pose
                val dx = startPose.tx() - endPose.tx()
                val dy = startPose.ty() - endPose.ty()
                val dz = startPose.tz() - endPose.tz()

                //euclidean distance
                anchorInfoBean.length = Math.sqrt((dx * dx + dy * dy + dz * dz).toDouble())

                drawLine(startAnchor, endAnchor, anchorInfoBean.length)
            } else {
                startNode = AnchorNode(hitResult.createAnchor())
                startNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)
                MaterialFactory.makeOpaqueWithColor(
                    this@MeasureActivity,
                    com.google.ar.sceneform.rendering.Color(0.33f, 0.87f, 0f)
                )
                    .thenAccept { material ->
                        val sphere = ShapeFactory.makeSphere(0.02f, Vector3.zero(), material)
                        sphereNodeArray.add(Node().apply {
                            setParent(startNode)
                            localPosition = Vector3.zero()
                            renderable = sphere
                        })
                    }
            }
        }
            else if(countTap == 6) {
                if (modelSize > esti_area) {
                    displayMsg("The 3d object doesn't fit in the given shape")
                    countTap--
                } else {
                    if (model == null) {
                        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
                        return@setOnTapArPlaneListener
                    }

                    // Create the Anchor.
                    val anchor = hitResult.createAnchor()
                    anchorNode3DObject = AnchorNode(anchor)
                    anchorNode3DObject.parent =
                        (UI_ArSceneView as MyArFragment).getArSceneView().getScene()
                    // Fix size of model:
                    //anchorNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

                    // Create the transformable model and add it to the anchor.
                    // Fix size of model:
                    //anchorNode.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));

                    // Create the transformable model and add it to the anchor.
                    val model =
                        TransformableNode((UI_ArSceneView as MyArFragment).getTransformationSystem())
                    model.parent = anchorNode3DObject
                    model.setRenderable(this.model)
                        .animate(true).start()

                    model.select()

                }
            }
    }

    }

    fun loadModels() {
        val weakActivity = WeakReference<MeasureActivity>(this)
        ModelRenderable.builder()
            .setSource(
                this,
                Uri.parse(modelLink)
            )
            .setIsFilamentGltf(true)
            .setAsyncLoadEnabled(true)
            .build()
            .thenAccept { model: ModelRenderable? ->
                var activity = weakActivity.get()
                if (activity != null) {
                    activity.model = model
                }
            }
            .exceptionally { throwable: Throwable? ->
                Toast.makeText(
                    this, "Unable to load model", Toast.LENGTH_LONG
                ).show()
                null
            }
    }

    private fun drawLine(firstAnchor: Anchor, secondAnchor: Anchor, length: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val firstAnchorNode = AnchorNode(firstAnchor)
            startNodeArray.add(firstAnchorNode)

            val secondAnchorNode = AnchorNode(secondAnchor)
            endNodeArray.add(secondAnchorNode)

            firstAnchorNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)
            secondAnchorNode.setParent((UI_ArSceneView as MyArFragment).arSceneView.scene)

            MaterialFactory.makeOpaqueWithColor(this@MeasureActivity, com.google.ar.sceneform.rendering.Color(0.53f, 0.92f, 0f))
                .thenAccept { material ->
                    val sphere = ShapeFactory.makeSphere(0.02f, Vector3(0.0f, 0.0f, 0.0f), material)
                    sphereNodeArray.add(Node().apply {
                        setParent(secondAnchorNode)
                        localPosition = Vector3.zero()
                        renderable = sphere
                    })
                }

            val firstWorldPosition = firstAnchorNode.worldPosition
            val secondWorldPosition = secondAnchorNode.worldPosition

            val difference = Vector3.subtract(firstWorldPosition, secondWorldPosition)
            val directionFromTopToBottom = difference.normalized()
            val rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up())

            MaterialFactory.makeOpaqueWithColor(this@MeasureActivity, com.google.ar.sceneform.rendering.Color(0.33f, 0.87f, 0f))
                .thenAccept { material ->
                    val lineMode = ShapeFactory.makeCube(Vector3(0.01f, 0.01f, difference.length()), Vector3.zero(), material)
                    val lineNode = Node().apply {
                        setParent(firstAnchorNode)
                        renderable = lineMode
                        worldPosition = Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5f)
                        worldRotation = rotationFromAToB
                    }
                    lineNodeArray.add(Node().apply {
                        setParent(firstAnchorNode)
                        renderable = lineMode
                        worldPosition = Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5f)
                        worldRotation = rotationFromAToB
                    })

                    ViewRenderable.builder()
                        .setView(this@MeasureActivity, R.layout.renderable_text)
                        .build()
                        .thenAccept { it ->
                            (it.view as TextView).text = "${String.format("%.1f", length * 100)} cm"
                            it.isShadowCaster = false
                            FaceToCameraNode().apply {
                                setParent(lineNode)
                                localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 90f)
                                localPosition = Vector3(0f, 0.02f, 0f)
                                renderable = it
                            }
                        }
                }
        }
    }

}