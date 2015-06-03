package core;

import com.google.common.primitives.Ints;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ArrayHandler {
	
	/**
	 * adds only positive values in an array with the positive values in another
	 * array. For example addPositiveIn2DArrays(new int[][] {{1,2},{-1,3}}, new
	 * int[][] {{0,0},{3,2}}) returns {{1,2},{-1,5}}.
	 * 
	 * @param arr1
	 *            the 1st 2D array.
	 * @param arr2
	 *            the 2nd 2D array.
	 * @return
	 */
	public static int[][] addPositiveIn2DArrays(int[][] arr1, int[][] arr2) {
		if (arr1 == null && arr2 != null) {
			return arr2;
        }
		if (arr1 != null && arr2 == null) {
			return arr1;
        }
		if (arr1.length != arr2.length || arr1[0].length != arr2[0].length) {
			return null;
        }
		int[][] finArr = new int[arr1.length][arr1[0].length];
		for (int i = 0; i <= finArr.length - 1; i++) {
			for (int j = 0; j <= finArr[0].length - 1; j++) {
				if (arr1[i][j] >= 0 && arr2[i][j] >= 0) {
					finArr[i][j] = arr1[i][j] + arr2[i][j];
                } else {
					finArr[i][j] = -1;
                }
            }
        }
		return finArr;
	}
	
	/**
	 * adds values in an array with the values in another array. The 2 arrays
	 * need to have same length.
	 * 
	 * @param col0
	 *            1st array.
	 * @param col1
	 *            2nd array.
	 * @return
	 */
	public static int[] addPositiveValuesInArrays(int[] col0, int[] col1) {
		if (col0.length != col1.length) {
			return null;
        }
		int[] finArr = new int[col0.length];
		for (int i = 0; i <= finArr.length - 1; i++) {
			if (col0[i]>=0 && col1[i]>=0) {
				finArr[i] = col0[i] + col1[i];
			} else {
				finArr[i] = -1;
			}
			
        }
		return finArr;
	}
	
	/**
	 * generates an integer random number between a given min and a given max (inclusive of min and max values).
	 * 
	 * @param max
	 * @param min
	 * @param random
	 * @return
	 */
	public static int generateRandomInt(int min, int max, Random random) {
		return (min + HardcodedData.random.nextInt(max-min+1));
	}
	
	/**
	 * gets the index of the last maximal value in a given array.
	 * 
	 * @param srcArray
	 *            the input array.
	 * @return index of the last maximal value in a given array.
	 */
	public static int getIndexOfMax(int[] srcArray) {
		int maxIdx = 0;
		for (int i = 0; i <= srcArray.length - 1; i++) {
			if (srcArray[i] > srcArray[maxIdx]) {
				maxIdx = i;
            }
        }
		return maxIdx;
	}
	
	/**
	 * gets the index of the last minimal value in a given array.
	 * 
	 * @param srcArray
	 *            the input array.
	 * @return index of the last minimal value in a given array.
	 */
	public static int getIndexOfMin(int[] srcArray) {
		int minIdx = 0;
		for (int i = 0; i <= srcArray.length - 1; i++) {
			if (srcArray[i] < srcArray[minIdx]) {
				minIdx = i;
            }
        }
		return minIdx;
	}
	
	/** 
	 * returns index of the minimum positive value in a given array.
	 * if all values in the given array is negative, return -1.
	 * 
	 * @param srcArray
	 * @return 
	 */
	public static int getIndexOfPositiveMin(double[] srcArray) {
		int minIdx = -1;
		for (int i = 0; i <= srcArray.length - 1; i++) {
			if (srcArray[i]>=0) {
				if (minIdx==-1) 
					minIdx = i;
				else {
					if (srcArray[i]<srcArray[minIdx]) {
						minIdx = i;
					}
				}
			}
		}
		
		return minIdx;
	}
	
	/**
	 * 
	 * @param myList
	 * @return
	 */
	public static int[] toInt(List<Integer> myList) {
		return ArrayUtils.toPrimitive(myList.toArray(new Integer[myList.size()]));
	}
	
	
	/**
	 * 
	 * @param set
	 * @return
	 */
	public static int[] toInt(Set<Integer> set) {
		int[] a = new int[set.size()];
		int i = 0;
		for (Integer val : set) a[i++] = val;
		return a;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	public static int getLength(int[] array) {
		if (array==null || array.length==0) return 0;
		else return array.length;
	}
	
	/**
	 * returns rms of positive values in array1 and array2.
	 * array1 and array2 must have same length. If not calculateRMS returns -1.
	 * If no pairs of corresponding values in array1 and array2 are both positive, for example array1 = {-1, 2, 3, -3} and array2 = {3, -1, -1, 2},
	 * calculateRMS(array1, array2) returns -1.
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static double calculateRMS(double[] array1, double[] array2) {
		double rms = -1;
		
		if (array1.length!=array2.length) return rms;
		
		int nValidElements = 0;
		for (int i=0; i<=array1.length-1; i++) {
			if (array1[i]>=0 && array2[i]>=0) {
				nValidElements += 1;
				if (rms<0) {
					rms = 0;
				}
				rms = rms + Math.pow(array1[i]-array2[i], 2.0);
			}
		}
		
		if (rms>-1) {
			rms = Math.pow(rms/(double)nValidElements, 0.5);
		}
		
		return rms;
	}
	
	/**
	 * returns rms of positive values in array1 and array2.
	 * array1 and array2 must have same length. If not calculateRMS returns -1.
	 * If no pairs of corresponding values in array1 and array2 are both positive, for example array1 = {-1, 2, 3, -3} and array2 = {3, -1, -1, 2},
	 * calculateRMS(array1, array2) returns -1.
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public static double calculateRMS(int[] array1, int[] array2) {
		double rms = -1;
		
		if (array1.length!=array2.length) return rms;
		
		int nValidElements = 0;
		for (int i=0; i<=array1.length-1; i++) {
			if (array1[i]>=0 && array2[i]>=0) {
				nValidElements += 1;
				if (rms<0) {
					rms = 0;
				}
				rms = rms + Math.pow(array1[i]-array2[i], 2.0);
			}
		}
		
		if (rms>-1) {
			rms = Math.pow(rms/(double)nValidElements, 0.5);
		}
		
		return rms;
	}
	
	/**
	 * removes elements equal to a specified value from an original array.
	 * 
	 * @param tmparray
	 *            the original integer array.
	 * @param remVal
	 *            the value to be removed.
	 * @return subset of the original array (with out elements equal to the
	 *         specified value)
	 */
	public static int[] removeValueInArray(int[] tmparray, int remVal) {
		int[] finarray = null;
		if (tmparray == null) {
			return finarray;
        }
		int[] idxVal = null;
		for (int i = 0; i < tmparray.length; i++) {
			if (tmparray[i] == remVal) {
				idxVal = ArrayUtils.add(idxVal, i);
            }
        }
		if (idxVal == null) {
			return tmparray;
        }
		int[] idxRemain = null;
		for (int i = 0; i < tmparray.length; i++) {
			if (ArrayUtils.indexOf(idxVal, i) == -1) {
				idxRemain = ArrayUtils.add(idxRemain, i);
            }
        }

		if (idxRemain == null) {
			return finarray;
        }

		finarray = extractArray(tmparray, idxRemain);

		return finarray;
	}
	
	
	/**
	 * creates an integer array from a source array. For example,
	 * extractArray(new int[] {1,3,5,6,7}, new int[] {0,3,4}) returns {1,6,7}.
	 * 
	 * @param srcArray
	 *            the source array.
	 * @param idxArray
	 *            index of elements in the source array that form the new array.
	 * @return
	 */
	public static int[] extractArray(int[] srcArray, int[] idxArray) {
		if (max(idxArray) > srcArray.length - 1) {
			return null;
        }
		int[] finArray = new int[idxArray.length];
		for (int i = 0; i <= idxArray.length - 1; i++) {
			finArray[i] = srcArray[idxArray[i]];
        }
		return finArray;
	}
	
	
	/**
	 * gets the maximal value in a given array.
	 * 
	 * @param srcArray
	 *            the input array.
	 * @return maximal value in a given array.
	 */
	public static int max(int[] srcArray) {
		int imax = getIndexOfMax(srcArray);
		return srcArray[imax];
	}
	
	/**
	 * normalises non-negative elements in an array. If an element in input array is negative, its value is -1.0 in the output double array.
	 * 
	 * @param array
	 * @return
	 */
	public static double[] normaliseArray(int[] array) {
		double[] normalisedArray = new double[array.length];
		
		int sum = sumPositiveInArray(array);
		if (sum==0) {
			for (int i=0; i<=array.length-1; i++) {
				if (array[i]<0) {
					normalisedArray[i] = (double)-1;
				} else {
					normalisedArray[i] = (double)0;
				}
			}
				
		} else {
			for (int i=0; i<=array.length-1; i++)
				if (array[i]<0) 
					normalisedArray[i] = (double)-1;
				else
					normalisedArray[i] = (double)array[i]/(double)sum;
		}
		
		return normalisedArray;
	}
	
	/**
	 * returns the sum of positive values in an integer array. For example,
	 * sumPositiveInArray(new int[] {-1,2,3}) returns 5;
	 * 
	 * @param srcArray
	 *            the source array.
	 * @return
	 */
	public static int sumPositiveInArray(int[] srcArray) {
		int sum = 0;
		for (int i = 0; i <= srcArray.length - 1; i++) {
			if (srcArray[i] > 0) {
				sum = sum + srcArray[i];
            }
        }
		return sum;
	}
	
	/**
	 * creates an array in which values increase linearly. For example
	 * makeIncrementalIntArray(1, 5, 2) returns {1,3,5}.
	 * 
	 * @param startVal
	 *            first value of the array.
	 * @param endVal
	 *            last value of the array.
	 * @param step
	 *            the increment step.
	 * @return
	 */
	public static int[] makeIncrementalIntArray(int startVal, int endVal, int step) {
		int length = (endVal - startVal) / step;
		if (length == 0) { //i.e. startVal == endVal
			return new int[] {startVal};
        }

		int[] intArr = new int[length + 1];
		for (int i = 0; i <= intArr.length - 1; i++) {
			intArr[i] = startVal + i * step;
        }

		return intArr;
	}
	
	/**
	 * concatenates 2 matrices m1 and m2 so that rows of m2 follow those of m1.
	 * Note that 2 matrices m1 and m2 must have the same number of columns.
	 * 
	 * @param tmp1
	 *            1st matrix.
	 * @param tmp2
	 *            2nd matrix.
	 * @return
	 */
	@SuppressWarnings("null")
	public static int[][] concateMatrices(int[][] tmp1, int[][] tmp2) {
		if (tmp1 == null && tmp2 != null) {
			return tmp2;
        }
		if (tmp2 == null && tmp1 != null) {
			return tmp1;
        }
		if (tmp2 == null && tmp1 == null) {
			return null;
        }
		if (tmp2 != null && tmp1 != null && tmp1[0].length != tmp2[0].length) {
			return null;
        }

		@SuppressWarnings({ "null", "null", "null" })
		int[][] tmpSum = new int[tmp1.length + tmp2.length][tmp1[0].length];
		for (int i = 0; i <= tmpSum.length - 1; i++) {
			if (i <= tmp1.length - 1) {
                System.arraycopy(tmp1[i], 0, tmpSum[i], 0, tmp1[0].length);
            } else {
                System.arraycopy(tmp2[i - tmp1.length], 0, tmpSum[i], 0, tmp2[0].length);
            }
        }
		return tmpSum;
	}
	
	/**
	 * sorts an array ascendingly and returns index of the sorted values. For
	 * example, sortedIndices(new int[] {1,5,2,-1,0}) returns {3,4,0,2,1}.
	 * 
	 * @param x
	 *            array to be sorted.
	 * @return index of the sorted values.
	 */
	public static int[] sortedIndices(int[] array) {
		Integer[] arrayInteger = new Integer[array.length];
		for (int i=0; i<=array.length-1; i++) {
			arrayInteger[i] = (Integer)array[i];
		}
		
		IntegerArrayIndexComparator comparator = new IntegerArrayIndexComparator(arrayInteger);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		int[] sortedIndexes = new int[indexes.length];
		for (int i=0; i<=sortedIndexes.length-1; i++) {
			sortedIndexes[i] = (int)indexes[i];
		}
		
		return sortedIndexes;
	}
	
	public static int[] sortedIndices(double[] array) {
		Double[] arrayDouble = new Double[array.length];
		for (int i=0; i<=array.length-1; i++) {
			arrayDouble[i] = (Double)array[i];
		}
		
		DoubleArrayIndexComparator comparator = new DoubleArrayIndexComparator(arrayDouble);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		int[] sortedIndexes = new int[indexes.length];
		for (int i=0; i<=sortedIndexes.length-1; i++) {
			sortedIndexes[i] = (int)indexes[i];
		}
		
		return sortedIndexes;
	}
	

	
	/**
	 * picks randomly non-repeating some elements from a source array and not in
	 * another array. For example, pickRandomFromArray(new int[] {1,2,3,4,5,6},
	 * new int[] {2,6}, 2) returns {1,5}.
	 * 
	 * @param srcArray
	 *            the source array where elements will be picked from.
	 * @param excludedArray
	 *            the array containing elements that must not be picked.
	 * @param nPick
	 *            number of array to be picked.
	 * @sRandom a random object generating controlled random numbers.
	 * @return a subset of the source array that have no elements as specified
	 *         in the excludedArray.
	 */
	public static int[] pickRandomFromArray(int[] srcArray, int[] excludedArray,
			int nPick, Random sRandom) {
		if (nPick <= 0 || srcArray == null || srcArray.length == 0) {
			return null;
        }
		int[] excluded = excludedArray;
		int[] finArr = new int[nPick];
		int[] remained = getNonCommonInArrays(srcArray,excluded);
		if (remained.length < nPick) {
			finArr = null;
        } else {
			for (int i = 0; i <= nPick - 1; i++) {
				if (remained.length == 1) {
					finArr[i] = remained[0];
                } else {
					//finArr[i] = remained[sRandom.nextInt(remained.length - 1)];
                	finArr[i] = remained[sRandom.nextInt(remained.length)];
                }
				excluded = ArrayUtils.add(excluded, finArr[i]);
				remained = getNonCommonInArrays(srcArray, excluded);
			}
		}
		return finArr;
	}
	
	/**
	 * returns values in an array (source array) that are not in another array
	 * (comparing array). For example getNonCommonInArrays(new int[]
	 * {1,2,3,6,8}, new String[] {3,7,2}) returns {1,6,8}.
	 * 
	 * @param array1
	 *            source array.
	 * @param array2
	 *            comparing array.
	 * @return values in source array that are not in comparing array.
	 */
	public static int[] getNonCommonInArrays(int[] array1, int[] array2) {
		int[] nonCommon = null;

		if (array2 == null) {
			return array1;
        }
		if (array1 == null) {
			return null;
        }

		for (int i1 = 0; i1 <= array1.length - 1; i1++) {
			boolean common = false;
			for (int i2 = 0; i2 <= array2.length - 1; i2++) {
				if (array1[i1] == array2[i2]) {
					common = true;
					break;
				}
            }
			if (!common) {
				nonCommon = ArrayUtils.add(nonCommon, array1[i1]);
            }
		}

		return nonCommon;
	}
	
	
	/**
	 * returns index of the non-negative value, after increased by 1, in srcArray that results in the lowest RMS between the distribution of 
	 * (the new) srcArray and comparedArray.
	 * The returned value is normally the index of the smallest non-negative value in srcArray.
	 * 
	 * @param comparedArray
	 * @param srcArray
	 * @return
	 */
	public static int getIndexOfLowestRMS(double[] comparedArray, int[] srcArray) {
		int smallestRMSIndex = -1;
		
		if (srcArray.length!=comparedArray.length) return smallestRMSIndex;
		
		double smallestRMS = 1e6;
		
		for (int i=0; i<=srcArray.length-1; i++) {
			if (comparedArray[i]<0 || srcArray[i]<0) continue;
			srcArray[i] = srcArray[i] + 1;
			
			double[] normSrcArray = normaliseArray(srcArray);
			
			double tmpRMS = calculateRMS(normSrcArray, comparedArray);
			
			if (smallestRMSIndex<0 || (smallestRMSIndex>=0 && tmpRMS<smallestRMS)) {
				smallestRMS = tmpRMS;
				smallestRMSIndex = i;
			}
			
			srcArray[i] = srcArray[i] - 1;
		}
		
		
		return smallestRMSIndex;
	}
	
	/**
	 * 
	 * @param solutions
	 * @return
	 */
	public static double[][] getParetoFrontValues(double[][] solutions) {
		double[] dim1 = new double[solutions.length];
		for (int i=0; i<=dim1.length-1; i++) {
			dim1[i] = solutions[i][0];
		}
		
		int[] sortedIdxDim1 = sortedIndices(dim1);
		
		double[][] sortedSolutions = new double[solutions.length][2];
		for (int i=0; i<=sortedIdxDim1.length-1; i++) {
			sortedSolutions[i][0] = solutions[sortedIdxDim1[i]][0];
			sortedSolutions[i][1] = solutions[sortedIdxDim1[i]][1];
		}

		HashMap<Integer,Integer> pFront = new HashMap<Integer,Integer>();
		
		Integer pPointCount = 0;
		Integer pPointIndex = sortedIdxDim1[0];
		pFront.put(pPointCount, pPointIndex);
		
		for (int i=1; i<=sortedIdxDim1.length-1; i++) {
			 double dim2Val = solutions[sortedIdxDim1[i]][1];
			 if (dim2Val<solutions[(int)pPointIndex][1]) {
				 pPointCount = pPointCount+1;
				 pPointIndex = (Integer)sortedIdxDim1[i];
				 pFront.put(pPointCount, pPointIndex);
			 }
		}
		
//		int[] pPointIndices = new int[pFront.size()];
//		for (int i=0; i<=pPointIndices.length-1; i++) {
//			pPointIndices[i] = (int)pFront.get((Integer)i);
//		}
		
		double[][] paretoFront = new double[pFront.size()][2];
		for (int i=0; i<=paretoFront.length-1; i++) {
			int pIndex = (int)pFront.get((Integer)i);
			paretoFront[i][0] = solutions[pIndex][0];
			paretoFront[i][1] = solutions[pIndex][1];
		}
		
		return paretoFront;
	}
	
	/**
	 * 
	 * @param solutions
	 * @return
	 */
	public static int[] getParetoFrontIndices(double[][] solutions) {
		double[] dim1 = new double[solutions.length];
		for (int i=0; i<=dim1.length-1; i++) {
			dim1[i] = solutions[i][0];
		}
		
		int[] sortedIdxDim1 = sortedIndices(dim1);
		
		double[][] sortedSolutions = new double[solutions.length][2];
		for (int i=0; i<=sortedIdxDim1.length-1; i++) {
			sortedSolutions[i][0] = solutions[sortedIdxDim1[i]][0];
			sortedSolutions[i][1] = solutions[sortedIdxDim1[i]][1];
		}

		HashMap<Integer,Integer> pFront = new HashMap<Integer,Integer>();
		
		Integer pPointCount = 0;
		Integer pPointIndex = sortedIdxDim1[0];
		pFront.put(pPointCount, pPointIndex);
		
		for (int i=1; i<=sortedIdxDim1.length-1; i++) {
			 double dim2Val = solutions[sortedIdxDim1[i]][1];
			 if (dim2Val<solutions[(int)pPointIndex][1]) {
				 pPointCount = pPointCount+1;
				 pPointIndex = (Integer)sortedIdxDim1[i];
				 pFront.put(pPointCount, pPointIndex);
			 }
		}
		
		int[] pPointIndices = new int[pFront.size()];
		for (int i=0; i<=pPointIndices.length-1; i++) {
			pPointIndices[i] = (int)pFront.get((Integer)i);
		}
		
//		double[][] paretoFront = new double[pFront.size()][2];
//		for (int i=0; i<=paretoFront.length-1; i++) {
//			int pIndex = (int)pFront.get((Integer)i);
//			paretoFront[i][0] = solutions[pIndex][0];
//			paretoFront[i][1] = solutions[pIndex][1];
//		}
		
		return pPointIndices;
	}
	
	
	public static boolean isInArray(int[] array, int queryVal) {
		for (int i=0; i<=array.length-1; i++) {
			if (queryVal==array[i]) {
				return true;
			}
		}
		return false;
	}
}