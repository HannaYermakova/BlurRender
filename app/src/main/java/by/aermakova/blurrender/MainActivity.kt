package by.aermakova.blurrender

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val BLUR_RADIUS_COEFFICIENT = 0.25
        private const val TIMEOUT = 300L
    }

    private val disposable = CompositeDisposable()
    private val volumeBlur: PublishSubject<Float> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val photo = image

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, volume: Int, p2: Boolean) {
                volumeBlur.onNext((volume * BLUR_RADIUS_COEFFICIENT).toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        disposable.add(
            volumeBlur.throttleWithTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.computation())
                .map {
                it.let { radius ->
                    return@map BlurBuilder.blur(
                        this,
                        BitmapFactory.decodeResource(resources, R.drawable.processed),
                        radius
                    )
                }
            }.observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { photo.setImageBitmap(it) },
                    { it.printStackTrace() }
                )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}