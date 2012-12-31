package meenakshi.project.meenakshiocr;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Deskew
{
	
	String TAG = "Deskew.java";
	// Representation of a line in the image.
	public class HougLine
	{
		//' Count of points in the line.
		public int Count;
		//' Index in Matrix.
		public int Index;
		//' The line is represented as all x,y that solve y*cos(alpha)-x*sin(alpha)=d
		public double Alpha;
		public double d;
	}

	// The Bitmap
	Bitmap cBmp;
	// The range of angles to search for lines
	double cAlphaStart = -20;
	double cAlphaStep = 0.2;
	int cSteps = 40 * 5;
	// Precalculation of sin and cos.
	double[] cSinA;
	double[] cCosA;
	// Range of d
	double cDMin;
	double cDStep = 1;
	int cDCount;
	// Count of points that fit in a line.
	int[] cHMatrix;

	//    ' Calculate the skew angle of the image cBmp.
	public double GetSkewAngle()
	{
		HougLine[] hl;
		int i;
		double sum = 0;
		int count = 0;

		//' Hough Transformation
		Calc();
		//' Top 20 of the detected lines in the image.
		hl = GetTop(20);
		//' Average angle of the lines
		for (i = 0; i < 19; i++)        
		{             sum += hl[i].Alpha;             count += 1;         }         
		return sum / count;    
	}     //    ' Calculate the Count lines in the image with most points.     
	 private HougLine[] GetTop(int Count)     
	 {        
		 HougLine[] hl;         
		 int j;         
		 HougLine tmp;         
		 int AlphaIndex, dIndex;         
		 hl = new HougLine[Count];         
		 for (int i = 0; i < Count; i++)         
		 {             
			 hl[i] = new HougLine();         
		 }         
		 for (int i = 0; i < cHMatrix.length - 1; i++)         
		 {             
			 if (cHMatrix[i] > hl[Count - 1].Count)
			 {
				 hl[Count - 1].Count = cHMatrix[i];
				 hl[Count - 1].Index = i;
				 j = Count - 1;
				 while (j > 0 && hl[j].Count > hl[j - 1].Count)
				 {
					 tmp = hl[j];
					 hl[j] = hl[j - 1];
					 hl[j - 1] = tmp;
					 j -= 1;
				 }
			 }
		 }
		 for (int i = 0; i < Count; i++)         
		 {            
			 dIndex = hl[i].Index / cSteps;            
			 AlphaIndex = hl[i].Index - dIndex * cSteps;             
			 hl[i].Alpha = GetAlpha(AlphaIndex);            
			 hl[i].d = dIndex + cDMin;         
		 }        
		 return hl;     
	 }     
	 public Deskew(Bitmap bmp)     
	 {         cBmp = bmp;     }     //    ' Hough Transforamtion:     
	 private void Calc()     
	 {        
		 int x;         
		 int y;         
		 int hMin = cBmp.getHeight() / 4;         
		 int hMax = cBmp.getHeight() * 3 / 4;        
		 Init();         
		 for (y = hMin; y < hMax; y++)        
		 {             
			 for (x = 1; x < cBmp.getWidth() - 2; x++)             
			 {                 //' Only lower edges are considered.                 
				 if (IsBlack(x, y) == true)                 
				 {                     
					 if (IsBlack(x, y + 1) == false)                   
					 {                        
						 Calc(x, y);               
					 }                
				 }          
			 }        
		 }  
	 }     //    ' Calculate all lines through the point (x,y).     
	 private void Calc(int x, int y)    
	 {         
		 double d;        
		 int dIndex;       
		 int Index;        
		 for (int alpha = 0; alpha < cSteps - 1; alpha++)      
		 {             
			 d = y * cCosA[alpha] - x * cSinA[alpha];           
			 dIndex = (int)CalcDIndex(d);            
			 Index = dIndex * cSteps + alpha;           
			 try            
			 {                 
				 cHMatrix[Index] += 1;             
			 }           
			 catch (Exception ex)            
			 {                 
				 Log.v(TAG, "in void Calc" + ex.toString());           
			 }        
		 }    
	 }    
	 private double CalcDIndex(double d)   
	 {        
		 //return Convert.ToInt32(d - cDMin);   
		 return (int)(d - cDMin);
	 }     
	 private boolean IsBlack(int x, int y)    
	 {         
		 //Color c, ccc;        
		 double luminance;         
		 int c = cBmp.getPixel(x, y);  
		 luminance = (Color.red(c) * 0.299) + (Color.green(c) * 0.587) + (Color.blue(c) * 0.114);     
		 return luminance < 140;     
	 }    
	 private void Init()   
	 {        
		 double angle;         //' Precalculation of sin and cos.         
		 cSinA = new double[cSteps - 1];       
		 cCosA = new double[cSteps - 1];         
		 for (int i = 0; i < cSteps - 1; i++)       
		 {             angle = GetAlpha(i) * Math.PI / 180.0;         
		 cSinA[i] = Math.sin(angle);           
		 cCosA[i] = Math.cos(angle);     
		 }         //' Range of d:        
		 cDMin = -cBmp.getWidth();        
		 cDCount = (int)(2 * (cBmp.getWidth() + cBmp.getHeight()) / cDStep);      
		 cHMatrix = new int[cDCount * cSteps];   
	 }     
	 public double GetAlpha(int Index)  
	 {         
		 return cAlphaStart + Index * cAlphaStep;     
	 }    
	 /*public static Bitmap RotateImage(Bitmap bmp, double angle)   
	 {         
		 Graphics g;        
		 Bitmap tmp = new Bitmap(bmp.Width, bmp.Height, PixelFormat.Format32bppRgb);      
		 tmp.SetResolution(bmp.HorizontalResolution, bmp.VerticalResolution);      
		 g = Graphics.FromImage(tmp);       
		 try     
		 {       
			 g.FillRectangle(Brushes.White, 0, 0, bmp.Width, bmp.Height);           
			 g.RotateTransform((float)angle);           
			 g.DrawImage(bmp, 0, 0);        
		 }       
		 finally    
		 {             
			 g.Dispose();         
		 }       
		 return tmp;     
	 }*/

}