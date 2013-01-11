package poodleDeveloper.karel;

import java.util.ArrayList;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.ActionMenuItem;

import poodleDeveloper.karel.data.KCasilla;
import poodleDeveloper.karel.data.Karel;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class KWorld extends SurfaceView implements SurfaceHolder.Callback{

	public KWorld(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private KThread thread;
	private Bitmap world,karelN,karelE,karelS,karelO;
	private Point size;
	
	private Point maxScreenXY;
	private Point minScreenXY;
	private Context context;
	private int lastX,lastY;
	private boolean estoyArrastrando = false;	
	
	@SuppressLint("NewApi")
	public void init(Context context) {
		//super(context);
		this.context = context;
		WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		
		world = BitmapFactory.decodeResource(getResources(), R.drawable.kworld);
		karelN = BitmapFactory.decodeResource(getResources(), R.drawable.knorte);
		karelE = BitmapFactory.decodeResource(getResources(), R.drawable.keste);
		karelS = BitmapFactory.decodeResource(getResources(), R.drawable.ksur);
		karelO = BitmapFactory.decodeResource(getResources(), R.drawable.koeste);
		
		getHolder().addCallback(this);
		maxScreenXY = new Point();
		minScreenXY = new Point();
		size = getDisplaySize(display);
		maxScreenXY.set(size.x/54+1, size.y/54);
		minScreenXY.set(0, 0);
		
		Toast.makeText(context, maxScreenXY.x+"x"+maxScreenXY.y, Toast.LENGTH_SHORT).show();
		initMenuItems();
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private static Point getDisplaySize(final Display display) {
	    final Point point = new Point();
	    try {
	        display.getSize(point);
	    } catch (java.lang.NoSuchMethodError ignore) {
	        point.x = display.getWidth();
	        point.y = display.getHeight(); 
	    }
	    return point;
	}
	
	private void initMenuItems(){
		
	}
	@Override
	public void onDraw(Canvas canvas){ 
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);
		
		for(float i = size.y; i > -54; i-=54)
			for(float j = 0; j < size.x+54; j+=54)
					canvas.drawBitmap(world, j, i, paint);
		
		if(Main.kworld.karel.posicion.fila < maxScreenXY.y+2 && Main.kworld.karel.posicion.fila > minScreenXY.y && 
				Main.kworld.karel.posicion.columna < maxScreenXY.x+3 && Main.kworld.karel.posicion.columna > minScreenXY.x)
			switch (Main.kworld.karel.orientacion) {
			case poodleDeveloper.karel.data.karelmovil.KWorld.NORTE:
				canvas.drawBitmap(karelN,
						(Main.kworld.karel.posicion.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
						(maxScreenXY.y-Main.kworld.karel.posicion.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
						paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.ESTE:
				canvas.drawBitmap(karelE,
						(Main.kworld.karel.posicion.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
						(maxScreenXY.y-Main.kworld.karel.posicion.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
						paint);
				break; 
			case poodleDeveloper.karel.data.karelmovil.KWorld.SUR:
				canvas.drawBitmap(karelS,
						(Main.kworld.karel.posicion.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
						(maxScreenXY.y-Main.kworld.karel.posicion.fila%(maxScreenXY.y-minScreenXY.y))*54+122,
						paint);
				break;
			case poodleDeveloper.karel.data.karelmovil.KWorld.OESTE:
				canvas.drawBitmap(karelO,
						(Main.kworld.karel.posicion.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+8,
						(maxScreenXY.y-Main.kworld.karel.posicion.fila%(maxScreenXY.y-minScreenXY.y))*54+1122,
						paint);
				break;
			default:
				break;
			}
		for(poodleDeveloper.karel.data.karelmovil.KCasilla c: Main.kworld.casillas.values()){ 
			if(c.fila < maxScreenXY.y+2 && c.fila > minScreenXY.y && c.columna < maxScreenXY.x+3 && c.columna > minScreenXY.x){
				if(c.paredes.size() > 0){
					paint.setColor(Color.BLACK);
					paint.setStrokeWidth(6);
					for(int p : c.paredes)
						switch(p){
						case poodleDeveloper.karel.data.karelmovil.KWorld.NORTE: 
							canvas.drawLine((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+3, 
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120, 
											(c.columna%(maxScreenXY.x-minScreenXY.x))*54+3, 
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120,
											paint); 
							break;
						case poodleDeveloper.karel.data.karelmovil.KWorld.ESTE:
							canvas.drawLine((c.columna%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120,
											(c.columna%(maxScreenXY.x-minScreenXY.x))*54+5,
											(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+120+54,
											paint);
							break;
						}
				}
				if(c.zumbadores > 0 ){
					paint.setColor(Color.GREEN); 
					paint.setStrokeWidth(18);
					canvas.drawCircle((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 8, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					if(c.zumbadores > 9)
						canvas.drawText(String.valueOf(c.zumbadores),
							(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
					else
						canvas.drawText(String.valueOf(c.zumbadores),
								(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+27,
								(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}else if(c.zumbadores == -1){
					paint.setColor(Color.GREEN);
					paint.setStrokeWidth(18);
					canvas.drawCircle((c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+31,
									 (maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+34,
									 8, paint);
					paint.setColor(Color.DKGRAY);
					paint.setStrokeWidth(1);
					canvas.drawText("-1",
							(c.columna%(maxScreenXY.x-minScreenXY.x)-1)*54+24,
							(maxScreenXY.y-c.fila%(maxScreenXY.y-minScreenXY.y))*54+112+37, paint);
				}
			}
		}
		
		
				
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub 
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new KThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while(retry){
			try{
				thread.join();
				retry = false;
			}catch(InterruptedException e){
				
			}
		}
		
	}
	
	public boolean onTouchEvent(MotionEvent event){
		int evento = event.getAction();
		switch (evento) {
		case MotionEvent.ACTION_DOWN:
			estoyArrastrando = true;
			lastX = (int)event.getX();
			lastY = (int)event.getY();
			//	 System.out.println(lastX+"  "+lastY);
			System.out.println("Max: "+maxScreenXY.x+"x"+maxScreenXY.y+"     Min: "+minScreenXY.x+"x"+minScreenXY.y);
			break;
		case MotionEvent.ACTION_MOVE:
			if(estoyArrastrando){
				int dx = ((int)(event.getX()) - lastX);
				int dy = ((int)(event.getY()) - lastY);
					maxScreenXY.x = (maxScreenXY.x+dx)%54;
					maxScreenXY.y = (maxScreenXY.y+dy)%54;
					minScreenXY.x = (minScreenXY.x+dx)%54;
					minScreenXY.y = (minScreenXY.y+dy)%54;
					lastX = (int)event.getX();
					lastY = (int)event.getY();
			}
			break;
		case MotionEvent.ACTION_UP:
			estoyArrastrando = false;
		default:
			break;
		}
		invalidate();
		return true;
	}

	public poodleDeveloper.karel.data.karelmovil.KWorld getKWorld(){
		return kworld;
	}
	
}
