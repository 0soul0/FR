package com.gj.fr

import android.app.Instrumentation
import android.app.usage.UsageEvents.Event
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.gj.arcoredraw.R
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar.cl_setting
import kotlinx.android.synthetic.main.activity_ar.iv_add
import kotlinx.android.synthetic.main.activity_ar.iv_setting
import kotlinx.android.synthetic.main.activity_ar.seekBar_pitch
import kotlinx.android.synthetic.main.activity_ar.seekBar_roll
import kotlinx.android.synthetic.main.activity_ar.seekBar_shift
import kotlinx.android.synthetic.main.activity_ar.tv_back
import kotlinx.android.synthetic.main.activity_ar.tv_hole
import kotlinx.android.synthetic.main.activity_ar.tv_hole_title
import kotlinx.android.synthetic.main.activity_ar.tv_info
import kotlinx.android.synthetic.main.activity_ar.tv_out
import kotlinx.android.synthetic.main.activity_ar.tv_out_title
import kotlinx.android.synthetic.main.activity_ar.tv_placing
import kotlinx.android.synthetic.main.activity_ar.tv_reset
import kotlinx.android.synthetic.main.activity_ar.tv_setting
import kotlinx.android.synthetic.main.activity_ar.tv_thickness
import kotlinx.android.synthetic.main.activity_ar.tv_thickness_title
import kotlinx.android.synthetic.main.activity_ar.view_scale


class ArActivity : AppCompatActivity(R.layout.activity_ar) {

    companion object {
        var frModel: FRBean? = null
        fun start(context: Context) {
            context.startActivity(Intent(context, ArActivity::class.java))
        }
    }

    private var fakeResult = mutableListOf(
        FRBean(
            sort = "排名1",
            type = "DN125|JIS16K|JIS B 2210",
            out = "",
            hole = "",
            thickness = "",
            screw = ""
        )
    )

    private var TAG: String = "ArActivity-AppCompatActivity"

    private lateinit var arFragment: ArFragment
    private lateinit var viewCenter: View

    private var point: Point? = null

    private var dotModel: ModelRenderable? = null
    private var frOuterModel: Renderable? = null
    private var frOuterNode = AnchorNode()
    private var frOuterDefaultDiameter = 0.25f //25cm 直徑
    private var frOuterDefaultHeight = 0.03f //3cm

    private var frInterModel: ModelRenderable? = null
    private var frInterNode = AnchorNode()
    private var frInterDefaultDiameter = 0.20f //25cm 直徑
    private var frInterDefaultHeight = 0.01f //1cm
    private val time: Float = 2.0f

    private var scene: Scene? = null
    private var camera: Camera? = null

    private var status: Status = Status.STATUS_MODEL_ADD
    private var dots = mutableListOf<Node>()
    private var dataArray = arrayListOf<AnchorInfoBean>()
    private var wheelView: WheelView? = null
    private val df = DecimalFormat("#.#")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindItem()
        init()
        bindEvent()
        preparation3DModel()

    }


    private fun floatToString(float: Float): String {
        return df.format(float).toString()
    }

    private fun bindItem() {
        arFragment =
            (supportFragmentManager.findFragmentById(R.id.UI_ArSceneView) as ArFragment).apply {
//                planeDiscoveryController.hide()
//                planeDiscoveryController.setInstructionView(null)
            }
//        arFragment.arSceneView.scene.addOnUpdateListener {
//            detectPlace()
//        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->

            Log.d(TAG, "addFrOuterModel: ${hitResult.createAnchor()}")
            when (status) {
                Status.STATUS_MODEL_ADD -> {

//                    frOuterNode= AnchorNode(hitResult.createAnchor())
//                    frOuterNode.renderable=frOuterModel
//                    scene!!.addChild(frOuterNode)
//                    addFrOuterModel()
                    status = Status.STATUS_DOT_ADD
                    iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_add_24))
                    tv_out_title.setTextColor(resources.getColor(R.color.black_low))
                    tv_out.setTextColor(resources.getColor(R.color.black))

                    tv_info.visibility = View.VISIBLE
                    tv_info.text = resources.getString(R.string.select_Flange_Circumference)

                }

                Status.STATUS_DOT_ADD -> {

                    val node = AnchorNode(hitResult.createAnchor())
                    node.renderable = dotModel
                    scene!!.addChild(node)
                    val anchorInfoBean = AnchorInfoBean("", hitResult.createAnchor(), 0.0)
                    dataArray.add(anchorInfoBean)

                    if (dataArray.size == 3) {
                        //計算球心https://www.cnblogs.com/kongbursi-2292702937/p/15190825.html
                        //改變模型大小和位置
                        val list = centerCircle3d(
                            dataArray[0].anchor.pose.tx().toDouble(),
                            dataArray[0].anchor.pose.ty().toDouble(),
                            dataArray[0].anchor.pose.tz().toDouble(),
                            dataArray[1].anchor.pose.tx().toDouble(),
                            dataArray[1].anchor.pose.ty().toDouble(),
                            dataArray[1].anchor.pose.tz().toDouble(),
                            dataArray[2].anchor.pose.tx().toDouble(),
                            dataArray[2].anchor.pose.ty().toDouble(),
                            dataArray[2].anchor.pose.tz().toDouble()
                        )

                        changeModelFrScale(list, hitResult.createAnchor())
                        tv_out.text = floatToString((list[3] * 1000 * time).toFloat())

                        status = Status.STATUS_HOLE_SIZE_SELECT
                        iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_arrow_forward_24))
                        tv_back.visibility = View.VISIBLE
                        tv_back.setOnClickListener {
                            it.visibility = View.GONE
                            dots.clear()
                            status = Status.STATUS_DOT_ADD

//                            tv_reset.performClick()
                        }

                        view_scale.setIsTouch(true)
                        view_scale.setValue(frInterDefaultDiameter * 1000 * frInterNode.worldScale.x)

                        tv_hole_title.setTextColor(resources.getColor(R.color.black_low))
                        tv_hole.setTextColor(resources.getColor(R.color.black))
                        tv_hole.text =
                            floatToString(frInterDefaultDiameter * 1000 * frInterNode.worldScale.x)

                        tv_info.visibility = View.VISIBLE
                        tv_info.text = resources.getString(R.string.map_red_cylinder)

                    }
                }
            }
        }


        viewCenter = findViewById(R.id.view_center)

        tv_back.visibility = View.GONE
        tv_info.visibility = View.GONE
        view_scale.setIsTouch(false)
    }

    fun detectPlace() {
        val frame = arFragment.arSceneView.arFrame
        val plances = frame!!.getUpdatedTrackables(Plane::class.java)
        for (plane in plances) {
            if (plane.trackingState == TrackingState.TRACKING) {

                val anchor = plane.createAnchor(plane.centerPose)
                frOuterNode = AnchorNode(anchor)
                frOuterNode.renderable = frOuterModel
                scene!!.addChild(frOuterNode)
                status = Status.STATUS_DOT_ADD
                iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_add_24))
                tv_out_title.setTextColor(resources.getColor(R.color.black_low))
                tv_out.setTextColor(resources.getColor(R.color.black))

                tv_info.visibility = View.VISIBLE
                tv_info.text = resources.getString(R.string.select_Flange_Circumference)
            }
        }
    }

    private fun init() {
        val display = windowManager.defaultDisplay
        point = Point()
        display.getRealSize(point)

        scene = arFragment.arSceneView.scene
        camera = scene?.camera

    }

    private fun restore() {
        tv_info.visibility = View.GONE
        tv_out_title.setTextColor(resources.getColor(R.color.gray))
        tv_out.setTextColor(resources.getColor(R.color.gray))
        tv_hole_title.setTextColor(resources.getColor(R.color.gray))
        tv_hole.setTextColor(resources.getColor(R.color.gray))
        tv_thickness_title.setTextColor(resources.getColor(R.color.gray))
        tv_thickness.setTextColor(resources.getColor(R.color.gray))
        view_scale.setIsTouch(false)
        tv_back.visibility = View.GONE
        iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_zoom_out_map_24))
    }

    var processPitch = 50
    var processRoll = 50
    var processShift = 50


    private fun bindEvent() {

        iv_setting.setOnClickListener {
//            LanguageActivity.start(this)
            if (cl_setting.visibility == View.VISIBLE) {
                cl_setting.visibility = View.GONE
                iv_setting.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_language))
            } else {
                cl_setting.visibility = View.VISIBLE
                iv_setting.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_language_1))
            }
        }

        seekBar_pitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

//                val pitch =frOuterNode
//                var q2 = Quaternion()
//                processPitch = if(progress>processPitch){
//                    q2 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), -2f)
//                    progress
//                }else{
//                    q2 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), -2f)
//                    progress
//                }
//
//
//
//
//                frOuterNode.worldRotation = Quaternion.multiply(pitch, q2)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBar_roll.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val roll = frOuterNode.worldRotation

                processRoll = if (progress > processRoll) {
                    roll.z++
                    progress
                } else {
                    roll.y--
                    progress
                }

                frOuterNode.worldRotation = roll
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBar_shift.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val postion = frOuterNode.worldPosition

                processShift = if (progress > processShift) {
                    postion.z++
                    progress
                } else {
                    postion.z--
                    progress
                }

                frOuterNode.worldPosition = postion
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        tv_placing.setOnClickListener {
            addFrOuterModel()
            status = Status.STATUS_DOT_ADD
        }

        iv_add.setOnClickListener {
            Log.d(TAG, "setOnClickListener: setOnClickListener")
            setAddEvent()

        }

//        iv_add.setOnLongClickListener {
//            if(status==Status.STATUS_MODEL_ADD){
//                detectPlace()
//            }
////
//            Log.d(TAG, "setOnLongClickListener: ")
//            true
//        }

        iv_add.setOnTouchListener { v, event ->
            if (status == Status.STATUS_MODEL_ADD) {
                if (event.action == MotionEvent.ACTION_DOWN) {
//                    detectPlace()
                    Log.d(TAG, "DOWN: ${status}")
                } else {
                    Log.d(TAG, "UP: ")
                }
                true
            } else {
                false
            }
        }


        view_scale.setValueChangeListener {
            when (status) {
                Status.STATUS_HOLE_SIZE_SELECT -> {
                    tv_hole.text = floatToString(it)

                    val lowerPosition = Vector3(
                        (it / time) / (frInterDefaultDiameter * 1000),
                        frInterNode.worldScale.y,
                        (it / time) / (frInterDefaultDiameter * 1000)
                    )
                    frInterNode.worldScale = lowerPosition
                    Log.d(TAG, "STATUS_HOLE_SIZE_SELECT: ${lowerPosition} ")
                }

                Status.STATUS_THICKNESS_SELECT -> {
                    tv_thickness.text = floatToString(it)
//
                    val lowerPosition =
                        Vector3(
                            frOuterNode.worldScale.x,
                            it / (frOuterDefaultHeight * 1000),
                            frOuterNode.worldScale.z
                        )
                    frOuterNode.worldScale = lowerPosition
                }

                Status.STATUS_SCREW_SELECT -> {}

            }
        }

        tv_reset.setOnClickListener {
            status = Status.STATUS_MODEL_ADD
            restore()
            scene!!.removeChild(frInterNode)
            frInterNode = AnchorNode()
            scene!!.removeChild(frOuterNode)
            frOuterNode = AnchorNode()
            dataArray = arrayListOf<AnchorInfoBean>()
        }
        tv_setting.setOnClickListener {
            TeachActivity.start(this)
        }
    }

    private fun preparation3DModel() {
//        Texture.builder()
//            .setSource(this, R.drawable.texture1)
//            .build()
//            .thenAccept(Consumer<Texture> { texture: Texture? ->
//                MaterialFactory.makeOpaqueWithTexture(this, texture)
//                    .thenAccept { material: Material? ->
//                        dotModel = ShapeFactory
//                            .makeSphere(
//                                0.005f,
//                                Vector3(0f, 0f, 0f),
//                                material
//                            )
//                    }
//            })

        MaterialFactory.makeTransparentWithColor(
            this,
            Color(ContextCompat.getColor(this, R.color.blue_dot))
        ).thenAccept { m ->

            dotModel = ShapeFactory
                .makeSphere(
                    0.005f,
                    Vector3(0f, 0f, 0f),
                    m
                )
        }

        Log.d(TAG, "preparation3DModel: preparation3DModel")
        MaterialFactory.makeTransparentWithColor(
            this,
            Color(ContextCompat.getColor(this, R.color.orange))
        ).thenAccept { m ->

            frOuterModel = ShapeFactory
                .makeCylinder(
                    frOuterDefaultDiameter,
                    frOuterDefaultHeight,  //3公分
                    Vector3.zero(),
                    m
                )
        }
        MaterialFactory.makeTransparentWithColor(
            this,
            Color(ContextCompat.getColor(this, R.color.red))
        )
            .thenAccept { m ->

                frInterModel = ShapeFactory
                    .makeCylinder(
                        frInterDefaultDiameter, //10公分
                        frInterDefaultHeight,  //10公分
                        Vector3.zero(),
                        m
                    )
                Log.d(TAG, "frInModel: ${frInterModel}")
            }

    }

    private fun addNodeToScene(anchor: Anchor, renderable: Renderable) {
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        scene?.addChild(anchorNode)
        node.select()
    }

    private fun setAddEvent() {
        restore()
        when (status) {
            Status.STATUS_MODEL_ADD -> {
//                addFrOuterModel()
//                simulateScreenTap(
//                    viewCenter.x + viewCenter.width / 2,
//                    viewCenter.x + viewCenter.width / 2
//                )
//                Log.d(TAG, "simulateScreenTap: ${viewCenter.x + viewCenter.width / 2}")
//                status = Status.STATUS_DOT_ADD
//                iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_add_24))
//                tv_out_title.setTextColor(resources.getColor(R.color.black_low))
//                tv_out.setTextColor(resources.getColor(R.color.black))
//
//                tv_info.visibility = View.VISIBLE
//                tv_info.text = "於法蘭面邊緣選取三個點"

            }

            Status.STATUS_DOT_ADD -> {
                simulateScreenTap(
                    viewCenter.x + viewCenter.width / 2,
                    viewCenter.x + viewCenter.width / 2
                )
                Log.d(TAG, "simulateScreenTap: ${viewCenter.x + viewCenter.width / 2}")
//                addPoint()
//                if (dots.size == 3) {
//                    //計算球心https://www.cnblogs.com/kongbursi-2292702937/p/15190825.html
//                    //改變模型大小和位置
//                    val list = centerCircle3d(
//                        dots[0].worldPosition.x.toDouble(),
//                        dots[0].worldPosition.y.toDouble(),
//                        dots[0].worldPosition.z.toDouble(),
//                        dots[1].worldPosition.x.toDouble(),
//                        dots[1].worldPosition.y.toDouble(),
//                        dots[1].worldPosition.z.toDouble(),
//                        dots[2].worldPosition.x.toDouble(),
//                        dots[2].worldPosition.y.toDouble(),
//                        dots[2].worldPosition.z.toDouble()
//                    )
//
//                    changeModelFrScale(list)
//                    tv_out.text = floatToString((list[3] * 1000).toFloat())
//
//                    status = Status.STATUS_HOLE_SIZE_SELECT
//                    iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_arrow_forward_24))
//                    tv_back.visibility = View.VISIBLE
//                    tv_back.setOnClickListener {
//                        it.visibility = View.GONE
//                        tv_reset.performClick()
//                    }
//
//                    view_scale.setIsTouch(true)
//                    view_scale.setValue(frInterDefaultDiameter*1000 * frInterNode.worldScale.x)
//
//                    tv_hole_title.setTextColor(resources.getColor(R.color.black_low))
//                    tv_hole.setTextColor(resources.getColor(R.color.black))
//                    tv_hole.text = floatToString(frInterDefaultDiameter*1000*frInterNode.worldScale.x)
//
//                    tv_info.visibility = View.VISIBLE
//                    tv_info.text = "透過轉盤調整紅色圓柱\n貼合其底面圓周與螺栓孔中心"
//
//                }
            }

            Status.STATUS_HOLE_SIZE_SELECT -> {

                status = Status.STATUS_THICKNESS_SELECT
                tv_back.visibility = View.GONE
                view_scale.setValue(30f)
                view_scale.setIsTouch(true)
                tv_thickness.text = "30"
                tv_thickness_title.setTextColor(resources.getColor(R.color.black_low))
                tv_thickness.setTextColor(resources.getColor(R.color.black))
                iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_arrow_forward_24))

                tv_info.visibility = View.VISIBLE
                tv_info.text = resources.getString(R.string.map_yellow_cylinder)

                frInterNode.setParent(null)
                frInterNode = AnchorNode()
            }

            Status.STATUS_THICKNESS_SELECT -> {


                status = Status.STATUS_SCREW_SELECT
                iv_add.setImageDrawable(resources.getDrawable(R.drawable.baseline_content_paste_24))
                val dialog = ScrewDialog({
                    wheelView = it
                }, {
                    iv_add.performClick()
                })
                dialog.show(supportFragmentManager, "")
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

//                val inputParameter = InputParameter(
//                    tv_out.text.toString().toDouble(),
//                    tv_hole.text.toString().toDouble(),
//                    tv_thickness.text.toString().toDouble(),
//                    wheelView!!.getCurrentItem()
//                )
//
//                query(inputParameter)?.forEach {
//                    fakeResult.add(
//                        FRBean(
//                            sort = "排名1",
//                            type = "DN125|JIS16K|JIS B 2210",
//                            out = "",
//                            hole = "",
//                            thickness = "",
//                            screw = ""
//                        )
//                    )
//                }


                showResultListDialog()

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

    private fun addFrOuterModel() {
        val ray = camera!!.screenPointToRay(
            viewCenter.x + viewCenter.width / 2,
            viewCenter.y + viewCenter.height / 2
        )


//        simulateScreenTap(viewCenter.x + viewCenter.width / 2, viewCenter.x + viewCenter.width / 2);

        frOuterNode.renderable = frOuterModel
        frOuterNode.setParent(scene)
        scene!!.addChild(frOuterNode)
//        //深度
        ray.getPoint(1f).also {
            //給node設定位置
            frOuterNode.worldPosition = it
        }


//        Log.d(TAG, "worldPosition: ${frOuterNode.worldPosition }")
//        val ray = camera!!.screenPointToRay(
//            viewCenter.x + viewCenter.width / 2,
//            viewCenter.y + viewCenter.height / 2
//        )
//        val result: HitTestResult = scene!!.hitTest(ray)
//        Log.d(TAG, "addFrOuterModel: ${result.point}")
//        frOuterNode.worldScale=result.point

//        frOuterNode.setParent(result.node)

//        frOuterNode.renderable=frOuterModel
//        scene!!.addChild(frOuterNode)

    }


    private fun simulateScreenTap(x: Float, y: Float) {
        // 创建 Instrumentation 对象
        val instrumentation = Instrumentation()

        // 获取当前时间
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()

        // 创建 ACTION_DOWN 事件
        val eventDown = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0)

        Thread { // 发送 ACTION_DOWN 事件
            instrumentation.sendPointerSync(eventDown)
        }.start()


        // 休眠一小段时间，模拟按住状态
        SystemClock.sleep(200)

        // 创建 ACTION_UP 事件
        val eventUp = MotionEvent.obtain(downTime, eventTime + 200, MotionEvent.ACTION_UP, x, y, 0)
        Thread {
            // 发送 ACTION_UP 事件
            instrumentation.sendPointerSync(eventUp)
        }.start()
    }


    private fun addPoint() {

        val ray = camera!!.screenPointToRay(
            viewCenter.x + viewCenter.width / 2,
            viewCenter.y + viewCenter.height / 2
        )
        val result: HitTestResult = scene!!.hitTest(ray)


        val node = Node()
        node.renderable = dotModel
        scene!!.addChild(node)
        node.worldPosition = result.point
        dots.add(node)

    }

    private fun changeModelFrScale(center: List<Double>, createAnchor: Anchor) {
        center.let {
//            scene!!.removeChild(frOuterNode)
            for (item in dots) {
                scene!!.removeChild(item)
            }

            Log.d(TAG, "changeModelFrScale: ${AnchorNode(createAnchor).worldPosition}")
            Log.d(TAG, "changeModelFrScale center: ${center}")

            val firstAnchorNode = AnchorNode(createAnchor)
            firstAnchorNode.setParent(arFragment.arSceneView.scene)

            val scaleX = it[3].toFloat() / frOuterDefaultDiameter
            val scaleY = firstAnchorNode.worldScale.y
            val scaleZ = it[3].toFloat() / frOuterDefaultDiameter

            frOuterNode = AnchorNode().apply {
                setParent(firstAnchorNode)
                renderable = frOuterModel?.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                worldPosition = Vector3(
                    it[0].toFloat(), it[1].toFloat(), it[2].toFloat()
                )
                worldScale = Vector3(scaleX, scaleY, scaleZ)
            }

//            frOuterNode= AnchorNode(createAnchor)
//            frOuterNode.renderable=frOuterModel
//            scene!!.addChild(frOuterNode)
////
//
//            frOuterNode.worldPosition = Vector3(
//                it[0].toFloat(), it[1].toFloat(), it[2].toFloat()
//            )
//
//            val scaleX = it[3].toFloat() / frOuterDefaultDiameter
//            val scaleY = frOuterNode.worldScale.y
//            val scaleZ = it[3].toFloat() / frOuterDefaultDiameter
//
//            frOuterNode.worldScale = Vector3(scaleX, scaleY, scaleZ)


            frInterNode = AnchorNode().apply {
                setParent(firstAnchorNode)
                renderable = frInterModel?.apply {
                    isShadowCaster = false
                    isShadowReceiver = false
                }
                worldPosition = Vector3(
                    it[0].toFloat(), it[1].toFloat() + 0.04f, it[2].toFloat()
                )
                val scaleXI = frInterNode.worldScale.x / time
                frInterNode.worldScale = Vector3(scaleXI, scaleY, scaleXI)
            }


//            frInterNode= AnchorNode(createAnchor)
//            frInterNode.renderable=frInterModel
//            scene!!.addChild(frInterNode)
//
//
//            frInterNode.renderable = frInterModel
//            scene!!.addChild(frInterNode)
//
//            //給node設定位置
//            frInterNode.worldPosition = Vector3(
//                it[0].toFloat(), it[1].toFloat()+0.01f, it[2].toFloat()
//            )
//            val scaleXI = (it[3].toFloat()*0.7) / frInterDefaultDiameter
//            frInterNode.worldScale = Vector3(scaleXI.toFloat(), scaleY, scaleXI.toFloat())


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


}

