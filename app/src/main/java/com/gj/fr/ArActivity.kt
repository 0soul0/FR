package com.gj.fr

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.FileProvider
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.gj.arcoredraw.R
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_ar.iv_add
import kotlinx.android.synthetic.main.activity_ar.tv_back
import kotlinx.android.synthetic.main.activity_ar.tv_hole
import kotlinx.android.synthetic.main.activity_ar.tv_hole_title
import kotlinx.android.synthetic.main.activity_ar.tv_info
import kotlinx.android.synthetic.main.activity_ar.tv_out
import kotlinx.android.synthetic.main.activity_ar.tv_out_title
import kotlinx.android.synthetic.main.activity_ar.tv_thickness
import kotlinx.android.synthetic.main.activity_ar.tv_thickness_title
import kotlinx.android.synthetic.main.activity_ar.view_scale
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.function.Consumer


class ArActivity : AppCompatActivity(R.layout.activity_ar) {

    companion object {
        var frModel: FRBean? = null
        fun start(context: Context) {
            context.startActivity(Intent(context, ArActivity::class.java))
        }
    }

    private var fakeResult = listOf(
        FRBean(
            sort = "排名1",
            type = "DN125|JIS16K|JIS B 2210",
            out = "",
            hole = "",
            thickness = "",
            screw = ""
        )
    )


    private lateinit var arFragment: ArFragment
    private lateinit var viewCenter: View

    private var point: Point? = null

    private var dotModel: ModelRenderable? = null
    private var scene: Scene? = null
    private var camera: Camera? = null

    private var status: Status = Status.STATUS_MODEL_ADD
    private var dots = mutableListOf<Node>()
    private var FRNode: Node = Node()
    private var FRcenterPosition: List<Double>? = null
    private var wheelView: WheelView? = null

    //    private val dataArray = arrayListOf<AnchorInfoBean>()
//    private  lateinit var startNode: AnchorNode
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindItem()
        init()
        bindEvent()
        buildDot()

    }

    private fun bindItem() {
        arFragment =
            (supportFragmentManager.findFragmentById(R.id.UI_ArSceneView) as ArFragment).apply {
                planeDiscoveryController.hide()
                planeDiscoveryController.setInstructionView(null)
            }
        viewCenter = findViewById(R.id.view_center)

        tv_back.visibility = View.GONE
        tv_info.visibility = View.GONE
        view_scale.setIsTouch(false)
    }

    private fun init() {
        val display = windowManager.defaultDisplay
        point = Point()
        display.getRealSize(point)

        scene = arFragment.arSceneView.scene
        camera = scene?.camera

    }

    private fun restore() {
        tv_out_title.setTextColor(resources.getColor(R.color.gray))
        tv_out.setTextColor(resources.getColor(R.color.gray))
        tv_hole_title.setTextColor(resources.getColor(R.color.gray))
        tv_hole.setTextColor(resources.getColor(R.color.gray))
        tv_thickness_title.setTextColor(resources.getColor(R.color.gray))
        tv_thickness.setTextColor(resources.getColor(R.color.gray))
        view_scale.setIsTouch(false)
    }

    private fun bindEvent() {

        iv_add.setOnClickListener {
            restore()
            when (status) {
                Status.STATUS_MODEL_ADD -> {
                    addFlangeOuterDiameter()

                    status = Status.STATUS_DOT_ADD
                    iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_add_24))
                    tv_out_title.setTextColor(resources.getColor(R.color.black_low))
                    tv_out.setTextColor(resources.getColor(R.color.black))

                    tv_info.text = "於法蘭面邊緣選取三個點"
                }

                Status.STATUS_DOT_ADD -> {
                    determineTheDot()
                    if (dots.size == 3 || true) {
                        //計算球心https://www.cnblogs.com/kongbursi-2292702937/p/15190825.html
//                        FRcenterPosition = centerCircle3d(
//                            dots[0].worldPosition.x.toDouble(),
//                            dots[0].worldPosition.y.toDouble(),
//                            dots[0].worldPosition.z.toDouble(),
//                            dots[1].worldPosition.x.toDouble(),
//                            dots[1].worldPosition.y.toDouble(),
//                            dots[1].worldPosition.z.toDouble(),
//                            dots[2].worldPosition.x.toDouble(),
//                            dots[2].worldPosition.y.toDouble(),
//                            dots[2].worldPosition.z.toDouble()
//                        )
                        //添加模型
                        moveFR()

                        status = Status.STATUS_HOLE_SIZE_SELECT
                        iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_arrow_forward_24))
                        tv_back.visibility = View.VISIBLE
                        view_scale.setValue(tv_hole.text.toString().toFloat())
                        view_scale.setIsTouch(true)
                        tv_hole_title.setTextColor(resources.getColor(R.color.black_low))
                        tv_hole.setTextColor(resources.getColor(R.color.black))
                        tv_info.text = "透過轉盤調整紅色圓柱\n貼合其底面圓周與螺栓孔中心"
                    }
                }

                Status.STATUS_HOLE_SIZE_SELECT -> {

                    status = Status.STATUS_THICKNESS_SELECT
                    tv_back.visibility = View.GONE
                    view_scale.setValue(tv_thickness.text.toString().toFloat())
                    view_scale.setIsTouch(true)
                    tv_thickness_title.setTextColor(resources.getColor(R.color.black_low))
                    tv_thickness.setTextColor(resources.getColor(R.color.black))
                    tv_info.text = "透過轉盤調整黃色圓柱\n使其厚度與法蘭相同"
                }

                Status.STATUS_THICKNESS_SELECT -> {


                    status = Status.STATUS_SCREW_SELECT
                    iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_content_paste_24))
                    ScrewDialog({
                        wheelView = it
                    }, {
                        iv_add.performClick()
                    }).show(supportFragmentManager, "")

                }

                Status.STATUS_SCREW_SELECT -> {
                    frModel = FRBean(
                        tv_out.text.toString(),
                        tv_hole.text.toString(),
                        tv_thickness.text.toString(),
                        wheelView!!.getCurrentItem(),
                        "",
                        ""
                    )

                    showResultListDialog()

                }
            }

        }

        view_scale.setValueChangeListener {
            when (status) {
                Status.STATUS_HOLE_SIZE_SELECT -> {
                    tv_hole.text = it.toString()
                }

                Status.STATUS_THICKNESS_SELECT -> {
                    tv_thickness.text = it.toString()
                }

                Status.STATUS_SCREW_SELECT -> {}

            }
        }
    }

    private fun showResultListDialog() {
        ResultListDialog(fakeResult) { data, dialog ->
            frModel?.let { fr ->
                fr.sort = data.sort
                fr.type = data.type
            }
            dialog.dismiss()
            ResultActivity.start(this)
        }.show(supportFragmentManager, "")
    }

    private fun addFlangeOuterDiameter() {
        ModelRenderable
            .builder()
            .setSource(this, Uri.parse("balloon.sfb"))
            .build()
            .thenAccept { renderable: ModelRenderable? ->

                FRNode.renderable = renderable
                scene!!.addChild(FRNode)
                val ray = camera!!.screenPointToRay(
                    viewCenter.x + viewCenter.width / 2,
                    viewCenter.y + viewCenter.height / 2
                )
                FRNode.worldPosition = ray.getPoint(1f)


            }
    }

    private fun buildDot() {
        Texture.builder()
            .setSource(this, R.drawable.texture1)
            .build()
            .thenAccept(Consumer<Texture> { texture: Texture? ->
                MaterialFactory.makeOpaqueWithTexture(this, texture)
                    .thenAccept { material: Material? ->
                        dotModel = ShapeFactory
                            .makeSphere(
                                0.01f,
                                Vector3(0f, 0f, 0f),
                                material
                            )
                    }
            })
    }

    private fun determineTheDot() {

        val ray = camera!!.screenPointToRay(
            viewCenter.x + viewCenter.width / 2,
            viewCenter.y + viewCenter.height / 2
        )
        val node = Node()
        node.renderable = dotModel
        scene!!.addChild(node)
        //深度
        ray.getPoint(1f).also {
            //給node設定位置
            node.worldPosition = it
            dots.add(node)
        }
    }

    private fun moveFR() {
        FRcenterPosition?.let {
            FRNode.worldPosition = Vector3(
                it[0].toFloat(), it[1].toFloat(), it[2].toFloat()
            )
        }

    }

    private fun centerCircle3d(
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double,
        x3: Double,
        y3: Double,
        z3: Double
    ): List<Double> {


        val a1 = y1 * z2 - y2 * z1 - y1 * z3 + y3 * z1 + y2 * z3 - y3 * z2
        val b1 = -(x1 * z2 - x2 * z1 - x1 * z3 + x3 * z1 + x2 * z3 - x3 * z2)
        val c1 = x1 * y2 - x2 * y1 - x1 * y3 + x3 * y1 + x2 * y3 - x3 * y2
        val d1 =
            -(x1 * y2 * z3 - x1 * y3 * z2 - x2 * y1 * z3 + x2 * y3 * z1 + x3 * y1 * z2 - x3 * y2 * z1)

        val a2 = 2 * (x2 - x1)
        val b2 = 2 * (y2 - y1)
        val c2 = 2 * (z2 - z1)
        val d2 = x1 * x1 + y1 * y1 + z1 * z1 - x2 * x2 - y2 * y2 - z2 * z2

        val a3 = 2 * (x3 - x1)
        val b3 = 2 * (y3 - y1)
        val c3 = 2 * (z3 - z1)
        val d3 = x1 * x1 + y1 * y1 + z1 * z1 - x3 * x3 - y3 * y3 - z3 * z3

        val x =
            (-(b1 * c2 * d3 - b1 * c3 * d2 - b2 * c1 * d3 + b2 * c3 * d1 + b3 * c1 * d2 - b3 * c2 * d1)
                    / (a1 * b2 * c3 - a1 * b3 * c2 - a2 * b1 * c3 + a2 * b3 * c1 + a3 * b1 * c2 - a3 * b2 * c1))
        val y =
            ((a1 * c2 * d3 - a1 * c3 * d2 - a2 * c1 * d3 + a2 * c3 * d1 + a3 * c1 * d2 - a3 * c2 * d1)
                    / (a1 * b2 * c3 - a1 * b3 * c2 - a2 * b1 * c3 + a2 * b3 * c1 + a3 * b1 * c2 - a3 * b2 * c1))
        val z =
            (-(a1 * b2 * d3 - a1 * b3 * d2 - a2 * b1 * d3 + a2 * b3 * d1 + a3 * b1 * d2 - a3 * b2 * d1)
                    / (a1 * b2 * c3 - a1 * b3 * c2 - a2 * b1 * c3 + a2 * b3 * c1 + a3 * b1 * c2 - a3 * b2 * c1))
        val radius =
            kotlin.math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y) + (z1 - z) * (z1 - z))

        return listOf(x, y, z, radius)
    }


    override fun onDestroy() {
        super.onDestroy()
        (arFragment as MyArFragment).onDestroy()
    }


//    lateinit var sceneView: ArSceneView
//
//    var modelNode: ArModelNode? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        registerView()
//    }
//
//   private fun registerView(){
//        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
//
//        }
//
//       modelNode?.anchor()
//       sceneView.planeRenderer.isVisible = false
//
//       newModelNode()
//    }
//
//    private fun newModelNode() {
//        modelNode?.takeIf { !it.isAnchored }?.let {
//            sceneView.removeChild(it)
//            it.destroy()
//        }
//
//        modelNode = ArModelNode(PlacementMode.BEST_AVAILABLE).apply {
//            applyPoseRotation = false
//
//
//
//            loadModelGlbAsync(
//                glbFileLocation = "models/halloween.glb",
//                autoAnimate = true,
//                //size
//                scaleToUnits = 0.01f,
//                centerOrigin = Position(y = -1.0f)
//            ) {
//                sceneView.planeRenderer.isVisible = true
//            }
//            onAnchorChanged = { anchor ->
//            }
//            onHitResult = { node, _ ->
//
//            }
//        }
//        sceneView.addChild(modelNode!!)
//        // Select the model node by default (the model node is also selected on tap)
//        sceneView.selectedNode = modelNode
//    }


//    lateinit var sceneView: ArSceneView
//    lateinit var loadingView: View
//    lateinit var statusText: TextView
//    lateinit var placeModelButton: ExtendedFloatingActionButton
//    lateinit var newModelButton: ExtendedFloatingActionButton
//
//    data class Model(
//        val fileLocation: String,
//        val scaleUnits: Float? = null,
//        val placementMode: PlacementMode = PlacementMode.BEST_AVAILABLE,
//        val applyPoseRotation: Boolean = true
//    )
//
//    val models = listOf(
//        Model("models/spiderbot.glb"),
//        Model(
//            fileLocation = "https://storage.googleapis.com/ar-answers-in-search-models/static/Tiger/model.glb",
//            // Display the Tiger with a size of 3 m long
//            scaleUnits = 2.5f,
//            placementMode = PlacementMode.BEST_AVAILABLE,
//            applyPoseRotation = false
//        ),
//        Model(
//            fileLocation = "https://sceneview.github.io/assets/models/DamagedHelmet.glb",
//            placementMode = PlacementMode.INSTANT,
//            scaleUnits = 0.5f
//        ),
//        Model(
//            fileLocation = "https://storage.googleapis.com/ar-answers-in-search-models/static/GiantPanda/model.glb",
//            placementMode = PlacementMode.PLANE_HORIZONTAL,
//            // Display the Tiger with a size of 1.5 m height
//            scaleUnits = 1.5f
//        ),
//        Model(
//            fileLocation = "https://sceneview.github.io/assets/models/Spoons.glb",
//            placementMode = PlacementMode.PLANE_HORIZONTAL_AND_VERTICAL,
//            // Keep original model size
//            scaleUnits = null
//        ),
//        Model(
//            fileLocation = "https://sceneview.github.io/assets/models/Halloween.glb",
//            placementMode = PlacementMode.PLANE_HORIZONTAL,
//            scaleUnits = 2.5f
//        ),
//    )
//    var modelIndex = 0
//    var modelNode: ArModelNode? = null
//
//    var isLoading = false
//        set(value) {
//            field = value
//            loadingView.isGone = !value
//        }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setFullScreen(
//            findViewById(R.id.rootView),
//            fullScreen = true,
//            hideSystemBars = false,
//            fitsSystemWindows = false
//        )
//
//        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar)?.apply {
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).topMargin = systemBarsInsets.top
//            }
//            title = ""
//        })
//        statusText = findViewById(R.id.statusText)
//        sceneView = findViewById<ArSceneView?>(R.id.sceneView).apply {
//            onArTrackingFailureChanged = { reason ->
//                statusText.text = reason?.getDescription(context)
//                statusText.isGone = reason == null
//            }
//        }
//        loadingView = findViewById(R.id.loadingView)
//        newModelButton = findViewById<ExtendedFloatingActionButton>(R.id.newModelButton).apply {
//            // Add system bar margins
//            val bottomMargin = (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
//            doOnApplyWindowInsets { systemBarsInsets ->
//                (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin =
//                    systemBarsInsets.bottom + bottomMargin
//            }
//            setOnClickListener { newModelNode() }
//        }
//        placeModelButton = findViewById<ExtendedFloatingActionButton>(R.id.placeModelButton).apply {
//            setOnClickListener { placeModelNode() }
//        }
//
//        newModelNode()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
////        menuInflater.inflate(R.menu.activity_main, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        item.isChecked = !item.isChecked
//        modelNode?.detachAnchor()
////        modelNode?.placementMode = when (item.itemId) {
////            R.id.menuPlanePlacement -> PlacementMode.PLANE_HORIZONTAL_AND_VERTICAL
////            R.id.menuInstantPlacement -> PlacementMode.INSTANT
////            R.id.menuDepthPlacement -> PlacementMode.DEPTH
////            R.id.menuBestPlacement -> PlacementMode.BEST_AVAILABLE
////            else -> PlacementMode.DISABLED
////        }
//        return super.onOptionsItemSelected(item)
//    }
//
//    fun placeModelNode() {
//        modelNode?.anchor()
//        sceneView.planeRenderer.isVisible = false
//    }
//
//    fun newModelNode() {
//        isLoading = true
//        modelNode?.takeIf { !it.isAnchored }?.let {
//            sceneView.removeChild(it)
//            it.destroy()
//        }
//
//        val model = models[modelIndex]
//        modelIndex = (modelIndex + 1) % models.size
//        modelNode = ArModelNode(model.placementMode).apply {
//            applyPoseRotation = model.applyPoseRotation
//            loadModelGlbAsync(
//                glbFileLocation = model.fileLocation,
//                autoAnimate = true,
//                scaleToUnits = model.scaleUnits,
//                // Place the model origin at the bottom center
//                centerOrigin = Position(y = -1.0f)
//            ) {
//                sceneView.planeRenderer.isVisible = true
//                isLoading = false
//            }
//            onAnchorChanged = { anchor ->
//                placeModelButton.isGone = anchor != null
//            }
//            onHitResult = { node, _ ->
//                placeModelButton.isGone = !node.isTracking
//            }
//        }
//        sceneView.addChild(modelNode!!)
//        // Select the model node by default (the model node is also selected on tap)
//        sceneView.selectedNode = modelNode
//    }
//

}

