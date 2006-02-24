/*
 * omeis.providers.re.quantum.QuantumFactory
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package omeis.providers.re.quantum;

//Java imports

//Third-party libraries

//Application-internal dependencies
import java.util.HashMap;
import java.util.Map;

import ome.model.display.QuantumDef;
import ome.model.enums.Family;
import ome.model.enums.PixelsType;

import tmp.PixelsConstants;

/** 
 * Factory to create objects to carry out quantization for a given context.
 * This class defines the constants to be used to identify a {@link QuantumMap}
 * within a quantization context.  It also defines the constants to be used to
 * define the bit depth of the quantized output interval.  
 * 
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  <br>Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:a.falconi@dundee.ac.uk">
 * 					a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision: 1.1 $ $Date: 2005/06/10 17:37:26 $)
 * </small>
 * @since OME2.2
 */
public class QuantumFactory
{
	
    //NOTE: The bit-depth constants cannot be modified b/c they have a meaning.
    
	/** Flag to select a 1-bit depth (<i>=2^1-1</i>) output interval. */
	public static final int   DEPTH_1BIT = 1;

    /** Flag to select a 2-bit depth (<i>=2^2-1</i>) output interval. */
	public static final int   DEPTH_2BIT = 3;
	
	/** Flag to select a 3-bit depth (<i>=2^3-1</i>) output interval. */
    public static final int   DEPTH_3BIT = 7;
	
    /** Flag to select a 4-bit depth (<i>=2^4-1</i>) output interval. */
	public static final int   DEPTH_4BIT = 15;
	
	/** Flag to select a 5-bit depth (<i>=2^5-1</i>) output interval. */
	public static final int   DEPTH_5BIT = 31;
	
	/** Flag to select a 6-bit depth (<i>=2^6-1</i>) output interval. */
	public static final int   DEPTH_6BIT = 63;
	
	/** Flag to select a 7-bit depth (<i>=2^7-1</i>) output interval. */
	public static final int   DEPTH_7BIT = 127;
	
	/** Flag to select a 8-bit depth (<i>=2^8-1</i>) output interval. */
	public static final int   DEPTH_8BIT = 255;
    
	/**
     * Flag to select a linear map for the quantization process. 
     * The equation of the map is of the form <i>y = a*x + b</i>.  The <i>a</i>
     * and <i>b</i> coefficients depend on the input and output (codomain) 
     * interval of the map.
     */
	public static final int   LINEAR = 0;

    /**
     * Flag to select a exponential map for the quantization process. 
     * The equation of the map is of the form <i>y = a*(exp(x^k)) + b</i>.  
     * The <i>a</i> and <i>b</i> coefficients depend on the input and output
     * (codomain) interval of the map.  The <i>k</i> coefficient is the one
     * specified by the {@link QuantumDef context}.
     */
    public static final int   EXPONENTIAL = 1;
	
    /**
     * Flag to select a logarithmic map for the quantization process. 
     * The equation of the map is of the form <i>y = a*log(x) + b</i>.  
     * The <i>a</i> and <i>b</i> coefficients depend on the input and output 
     * (codomain) interval of the map.
     */
	public static final int   LOGARITHMIC = 2;
	
    /**
     * Flag to select a polynomial map for the quantization process. 
     * The equation of the map is of the form <i>y = a*x^k + b</i>.  
     * The <i>a</i> and <i>b</i> coefficients depend on the input and output
     * (codomain) interval of the map.  The <i>k</i> coefficient is the one
     * specified by the {@link QuantumDef context}.
     * Note that {@link #LINEAR} is a special case of polynomial (<i>k = 1</i>).
     * We keep the {@link #LINEAR} constant for some UI reason but we apply the
     * same algorithm.
     */
	public static final int   POLYNOMIAL = 3;

    private static final Map familyNames = new HashMap();
    static {
        familyNames.put("LINEAR",Integer.valueOf(LINEAR));
        familyNames.put("EXPONENTIAL",Integer.valueOf(EXPONENTIAL));
        familyNames.put("LOGARITHMIC",Integer.valueOf(LOGARITHMIC));
        familyNames.put("POLYNOMIAL",Integer.valueOf(POLYNOMIAL));
    }
    
    public static int convertFamilyType(Family type)
    {
        return ((Integer) familyNames.get(type.getValue())).intValue();
    }
    
    /** Default value. */
    public static final boolean NOISE_REDUCTION = true;
    
	/**
     * Verifies that <code>qd</code> is not <code>null</code> and has been
     * properly defined.
     * 
     * @param qd The definition to verify.
     * @throws IllegalArgumentException If the check fails.
     */
    private static void verifyDef(QuantumDef qd, PixelsType type)
    {
        if (qd == null)    
            throw new NullPointerException("No quantum definition.");
        verifyBitResolution(qd.getBitResolution().intValue());
        verifyPixelType(PixelsConstants.convertPixelType(type));
    }
    
    /**
     * Verifies that <code>bitResolution</code> is one of the constants defined
     * by this class.
     * 
     * @param bitResolution The value to verify.
     * @throws IllegalArgumentException If the check fails.
     */
    private static void verifyBitResolution(int bitResolution)
    {
        boolean  b = false;
        switch (bitResolution) {
            case DEPTH_1BIT: b = true; break;
            case DEPTH_2BIT: b = true; break;
            case DEPTH_3BIT: b = true; break;
            case DEPTH_4BIT: b = true; break;
            case DEPTH_5BIT: b = true; break;
            case DEPTH_6BIT: b = true; break;
            case DEPTH_7BIT: b = true; break;
            case DEPTH_8BIT: b = true; break;
        }
        if (!b) throw new IllegalArgumentException(
                            "Unsupported bit resolution: "+bitResolution+".");
    }
    
    /**
     * Verifies that <code>pixelType</code> is one of the constants defined
     * by {@link PixelsConstants}.
     * 
     * @param pixelType    The value to verify.
     * @throws IllegalArgumentException If the check fails.
     */
    private static void verifyPixelType(int pixelType)
    {
        boolean b = false;
        switch (pixelType) {
            case PixelsConstants.BIT:    b = true; break;
            case PixelsConstants.INT8:   b = true; break;
            case PixelsConstants.INT16:  b = true; break;
            case PixelsConstants.INT32:  b = true; break;
            case PixelsConstants.UINT8:  b = true; break;
            case PixelsConstants.UINT16: b = true; break;
            case PixelsConstants.UINT32: b = true; break;
            case PixelsConstants.FLOAT:  b = true; break;
            case PixelsConstants.DOUBLE: b = true;
        }
        if (!b) throw new IllegalArgumentException(
                                    "Unsupported pixel type: "+pixelType+".");
    }
    
    /** 
     * Creates a {@link QuantumStrategy} object suitable for the pixels type
     * specified by <code>pd</code>.
     * 
     * @param qd Defines the quantization context.
     * @return A {@link QuantumStrategy} object suitable for the
     *          given pixels type.
     */
    private static QuantumStrategy getQuantization(QuantumDef qd, PixelsType type)
    {
        QuantumStrategy qs = null;
        switch (PixelsConstants.convertPixelType(type)) {
            case PixelsConstants.INT8:
            case PixelsConstants.UINT8:
            case PixelsConstants.INT16:
            case PixelsConstants.UINT16:
            case PixelsConstants.INT32:  
            case PixelsConstants.UINT32:   
            case PixelsConstants.FLOAT:  
            case PixelsConstants.DOUBLE: 
                qs = new Quantization_8_16_bit(qd,type);
                break;
            case PixelsConstants.BIT:    
            break;    
        }
        return qs;
    }
    
    /**
     * Returns a strategy to carry out the quantization process whose context
     * is defined by <code>pd</code>.
     * 
     * @param qd    Defines the quantization context.  Mustn't be 
     *              <code>null</code> and its values must have been
     *              properly specified.
     * @return  A {@link QuantumStrategy} suitable for the specified context. 
     */
	public static QuantumStrategy getStrategy(QuantumDef qd, PixelsType type)
	{
        verifyDef(qd, type);
        QuantumStrategy strg = null;
        strg = getQuantization(qd,type);
        if (strg == null)
            throw new IllegalArgumentException("Unsupported strategy");
        return strg;
	}

}
