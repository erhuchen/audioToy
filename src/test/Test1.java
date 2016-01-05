package test;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Test1 {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		double[] input = new double[]{1,2,3,4};
		Complex[] output = fft.transform(input, TransformType.FORWARD);
		for(int i = 0 ;i<output.length;i++){
			System.out.println(output[i]);
		}
		
	}

}

