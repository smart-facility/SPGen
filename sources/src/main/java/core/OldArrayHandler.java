package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.primitives.Ints;

public class OldArrayHandler {
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
	 * 
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
	 * 
	 * @param array
	 * @return
	 */
	public static int getLength(int[] array) {
		if (array==null || array.length==0) return 0;
		else return array.length;
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
	 * gets the unique values out of an integer array (in which values may
	 * repeat). For example, getUnique(new int[] {1,2,1,3}) returns {1,2,3}.
	 * 
	 * @param srcArray
	 *            input array.
	 * @return unique values of an integer array.
	 */
	public static int[] getUnique(int[] srcArray) {

        List<Integer> uArray = new ArrayList<Integer>();
        for (int aValue : srcArray) {
            if (uArray.contains(aValue)) {
                continue;
            }
            uArray.add(aValue);
        }
		return Ints.toArray(uArray);
	}

	/**
	 * rounds a double number to the closest integer. For example
	 * roundToCloserInt(0.5) returns 1 or roundToCloserInt(-1.7) returns 2.
	 * 
	 * @param val
	 *            input double number.
	 * @return the integer closest to val.
	 */
	public static int roundToCloserInt(double val) {
		if (val > 0) {
			return (int) (val + 0.5);
        } else {
			return (int) (val - 0.5);
        }
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
		if (length == 0) {
			return null;
        }

		int[] intArr = new int[length + 1];
		for (int i = 0; i <= intArr.length - 1; i++) {
			intArr[i] = startVal + i * step;
        }

		return intArr;
	}

	/**
	 * creates an array in which values are the same. For example
	 * makeIncrementalIntArray(3, 2) returns {2,2,2}.
	 * 
	 * @param size
	 *            size of the array.
	 * @param val
	 *            value of each element in the array.
	 * @return
	 */
	public static int[] makeUniformArray(int size, int val) {
		int[] finArray = new int[size];
		for (int i = 0; i <= finArray.length - 1; i++) {
			finArray[i] = val;
        }
		return finArray;
	}

	/**
	 * creates an integer array from a source array. For example,
	 * extractArray(new int[] {1,3,5,6,7}, 1, 3) returns {3,5,6}.
	 * 
	 * @param srcArray
	 *            the source array.
	 * @param iStart
	 *            index in the source array where the extraction starts.
	 * @param iEnd
	 *            index in the source array where the extraction ends.
	 * @return
	 */
	public static int[] extractArray(int[] srcArray, int iStart, int iEnd) {
        // copyOfRange "to" parameter is exclusive but we want inclusive so increment by one.
        return Arrays.copyOfRange(srcArray, iStart, iEnd + 1);
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
	 * returns a column in a 2D array. If the array is null or if the index
	 * specified is greater than the number of columns, return null.
	 * 
	 * @param srcArray
	 *            the source array.
	 * @param colIdx
	 *            index of the column.
	 * @return
	 */
	public static int[] getColumn(int[][] srcArray, int colIdx) {
		if (colIdx >= srcArray[0].length) {
			return null;
        }

		int[] intCol = null;
		for (int i = 0; i <= srcArray.length - 1; i++) {
			intCol = ArrayUtils.add(intCol, srcArray[i][colIdx]);
        }

		return intCol;
	}

	/**
	 * returns columns in a 2D array. If the array is null or if any of the
	 * column indices specified is greater than the number of columns, return
	 * null.
	 * 
	 * @param srcArray
	 *            the source array
	 * @param colIdx
	 *            index of columns to be extracted.
	 * @return
	 */
	public static int[][] getColumns(int[][] srcArray, int[] colIdx) {
		if (colIdx[getIndexOfMax(colIdx)] > srcArray[0].length) {
			return null;
        }
		int[][] finArr = new int[srcArray.length][colIdx.length];
		for (int i = 0; i <= finArr.length - 1; i++) {
			for (int j = 0; j <= colIdx.length - 1; j++) {
				finArr[i][j] = srcArray[i][colIdx[j]];
            }
        }
		return finArr;
	}

	/**
	 * replaces a column in a 2D source array by a 1D array. If the new 1D array
	 * is null or the length is different than the number of rows of the source
	 * array, returns the source array.
	 * 
	 * @param srcArray
	 *            the source array.
	 * @param colIdx
	 *            index of the column to be replaced.
	 * @param newCol
	 *            new array replacing the specified column.
	 * @return
	 */
	public static int[][] replaceColumn(int[][] srcArray, int colIdx, int[] newCol) {
		if (newCol == null) {
			return srcArray;
        }
		if (colIdx > srcArray[0].length - 1 || newCol.length != srcArray.length) {
			return srcArray;
        }
		int[][] finArray = srcArray;
		for (int i = 0; i <= newCol.length - 1; i++) {
			finArray[i][colIdx] = newCol[i];
        }
		return finArray;
	}

	/**
	 * puts 2 columns together to form a 2D array.
	 * 
	 * @param col0
	 *            first column.
	 * @param col1
	 *            second column.
	 * @return
	 */
	public static int[][] concateColumns(int[] col0, int[] col1) {
		if (col0.length != col1.length) {
			return null;
        }
		int[][] finArr = new int[col0.length][2];
		for (int i = 0; i <= col0.length - 1; i++) {
			finArr[i][0] = col0[i];
			finArr[i][1] = col1[i];
		}
		return finArr;
	}

	/**
	 * concatenates a new column after the last column in a 2d array.
	 * 
	 * @param srcArr
	 *            the source array which the new column will be added.
	 * @param col
	 *            the new column to be added.
	 * @return
	 */
	public static int[][] concateColumns(int[][] srcArr, int[] col) {
		if (srcArr.length != col.length)
			return null;
		int[][] finArr = new int[srcArr.length][srcArr[0].length + 1];
		for (int i = 0; i <= finArr.length - 1; i++) {
            System.arraycopy(srcArr[i], 0, finArr[i], 0, srcArr[0].length);
			finArr[i][finArr[0].length - 1] = col[i];
		}
		return finArr;
	}

	/**
	 * subtracts values in an array by the values in another array. The 2 arrays
	 * need to have same length.
	 * 
	 * @param col0
	 *            the subtracted array.
	 * @param col1
	 *            the subtracting array.
	 * @return
	 */
	public static int[] subtractValuesInColumns(int[] col0, int[] col1) {
		if (col0.length != col1.length) {
			return null;
        }
		int[] finArr = new int[col0.length];
		for (int i = 0; i <= finArr.length - 1; i++) {
			finArr[i] = col0[i] - col1[i];
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
	public static int[] addValuesInColumns(int[] col0, int[] col1) {
		if (col0.length != col1.length) {
			return null;
        }
		int[] finArr = new int[col0.length];
		for (int i = 0; i <= finArr.length - 1; i++) {
			finArr[i] = col0[i] + col1[i];
        }
		return finArr;
	}

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
		int[] remained = getNonCommonInArrays(srcArray,
				excluded);
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
	 * returns the index (of row and column) of the last element in an array
	 * corresponding to the value closest to a specified value. symmetric is
	 * true if tmparray is symmetric and false if not.
	 * 
	 * @param tmparray
	 *            the source array.
	 * @param val
	 *            the query value.
	 * @param symmetric
	 *            true if the source array is symmetric, false if otherwise.
	 * @return index of row and column of the element closest to the query
	 *         value.
	 */
	public static int[] getIndexofClosestToValue(int[][] tmparray, int val,
			boolean symmetric) {
		if (tmparray == null)
			return null;
		int[] finIdx = new int[2];
		if (symmetric) {
			finIdx[0] = 0;
			finIdx[1] = 1;
			for (int i = 0; i <= tmparray.length - 1; i++) {
				for (int j = i + 1; j <= tmparray[0].length - 1; j++) {
					if (Math.abs(tmparray[i][j] - val) < Math.abs(tmparray[finIdx[0]][finIdx[1]] - val)) {
						finIdx[0] = i;
						finIdx[1] = j;
					}
                }
            }
		} else {
			finIdx[0] = 0;
			finIdx[1] = 0;
			for (int i = 0; i <= tmparray.length - 1; i++) {
				for (int j = 0; j <= tmparray[0].length - 1; j++) {
					if (Math.abs(tmparray[i][j] - val) < Math.abs(tmparray[finIdx[0]][finIdx[1]] - val)) {
						finIdx[0] = i;
						finIdx[1] = j;
					}
                }
            }
		}

		return finIdx;
	}

	/**
	 * returns the index in srcArray of the value in a source array that is just
	 * greater than a specified value. For example, getIdxOfNextGreaterValue(new
	 * int[] {1,5,3,10}, 4) returns 1.
	 * 
	 * @param srcArray
	 *            the source array.
	 * @param minVal
	 *            the query value.
	 * @return index of the element that is just greater than the query value.
	 */
	public static int getIdxOfNextGreaterValue(int[] srcArray, int minVal) {
		if (max(srcArray) - minVal < 0) {
			return -1;
        }
		int iMinPosDif = getIndexOfMax(srcArray);
		for (int i = 0; i <= srcArray.length - 1; i++) {
			if (srcArray[i] - minVal < 0) {
				continue;
            }
			if (srcArray[i] - minVal < srcArray[iMinPosDif] - minVal) {
				iMinPosDif = i;
            }
		}
		return iMinPosDif;
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

	public static int[] sortedIndices(double[] x) {
		int[] ix = new int[x.length];
		int[] scratch = new int[x.length];
		for (int i = 0; i < ix.length; i++) {
			ix[i] = i;
		}
		mergeSortIndexed(x, ix, scratch, 0, x.length - 1);
		return ix;
	}

	/**
	 * sorts an array ascendingly and returns index of the sorted values. For
	 * example, sortedIndices(new int[] {1,5,2,-1,0}) returns {3,4,0,2,1}.
	 * 
	 * @param x
	 *            array to be sorted.
	 * @return index of the sorted values.
	 */
	public static int[] sortedIndices(int[] x) {
		int[] ix = new int[x.length];
		int[] scratch = new int[x.length];
		for (int i = 0; i < ix.length; i++) {
			ix[i] = i;
		}
		mergeSortIndexed(x, ix, scratch, 0, x.length - 1);
		return ix;
	}

	private static void mergeSortIndexed(double[] x, int[] ix, int[] scratch,
			int lo, int hi) {
		if (lo == hi) {
			return;
        }
		int mid = (lo + hi + 1) / 2;
		mergeSortIndexed(x, ix, scratch, lo, mid - 1);
		mergeSortIndexed(x, ix, scratch, mid, hi);
		mergeIndexed(x, ix, scratch, lo, mid - 1, mid, hi);
	}

	private static void mergeSortIndexed(int[] x, int[] ix, int[] scratch,
			int lo, int hi) {
		if (lo == hi) {
			return;
        }
		int mid = (lo + hi + 1) / 2;
		mergeSortIndexed(x, ix, scratch, lo, mid - 1);
		mergeSortIndexed(x, ix, scratch, mid, hi);
		mergeIndexed(x, ix, scratch, lo, mid - 1, mid, hi);
	}

	private static void mergeIndexed(double[] x, int[] ix, int[] scratch,
			int lo1, int hi1, int lo2, int hi2) {
		int i = 0;
		int i1 = lo1;
		int i2 = lo2;
		int n1 = hi1 - lo1 + 1;
		while (i1 <= hi1 && i2 <= hi2) {
			if (x[ix[i1]] <= x[ix[i2]]) {
				scratch[i++] = ix[i1++];
            } else {
				scratch[i++] = ix[i2++];
            }
		}
		while (i1 <= hi1) {
			scratch[i++] = ix[i1++];
        }
		while (i2 <= hi2) {
			scratch[i++] = ix[i2++];
        }
        System.arraycopy(scratch, 0, ix, lo1, hi1 + 1 - lo1);
        System.arraycopy(scratch, n1, ix, lo2, hi2 + 1 - lo2);
	}

	private static void mergeIndexed(int[] x, int[] ix, int[] scratch, int lo1,
			int hi1, int lo2, int hi2) {
		int i = 0;
		int i1 = lo1;
		int i2 = lo2;
		int n1 = hi1 - lo1 + 1;
		while (i1 <= hi1 && i2 <= hi2) {
			if (x[ix[i1]] <= x[ix[i2]]) {
				scratch[i++] = ix[i1++];
            } else {
				scratch[i++] = ix[i2++];
            }
		}
		while (i1 <= hi1) {
			scratch[i++] = ix[i1++];
        }
		while (i2 <= hi2) {
			scratch[i++] = ix[i2++];
        }
        System.arraycopy(scratch, 0, ix, lo1, hi1 + 1 - lo1);
        System.arraycopy(scratch, n1, ix, lo2, hi2 + 1 - lo2);
	}

	/**
	 * allocates values into an integer array so that the proportion of each
	 * element is as in a reference value and the sum of all elements equals to
	 * a specified value. Any negative values in the reference array will be
	 * ignored. For example, allocateProportionally(new int[] {1,5,2,-1,0},10)
	 * returns {1,6,3,-1,0}.
	 * 
	 * @param srcArr
	 *            the reference array.
	 * @param sum
	 *            the value which the sum of the non-negative elements in the
	 *            final array must be equal to.
	 * @return an integer array satisfying the above conditions.
	 */
	public static int[] allocateProportionally(int[] srcArr, int sum) {
		int[] finArr = new int[srcArr.length];

		for (int i = 0; i <= finArr.length - 1; i++) {
			if (srcArr[i] <= 0) {
				finArr[i] = srcArr[i];
            } else {
				finArr[i] = roundToCloserInt((double) srcArr[i]
						/ (double) sumPositiveInArray(srcArr) * sum);
            }
        }

		int tstSum = sumPositiveInArray(finArr);

		if (tstSum > sum) {
			int[] indices = sortedIndices(finArr);
			for (int i = indices.length - 1; i >= 0; i--) {
				if (finArr[indices[i]] > 0) {
					finArr[indices[i]] = finArr[indices[i]] - 1;
					tstSum = tstSum - 1;
					if (tstSum == sum) {
						break;
                    }
				}
            }
		}
		if (tstSum < sum) {
			int[] indices = sortedIndices(finArr);
			for (int i = 0; i <= indices.length - 1; i++) {
				if (finArr[indices[i]] > 0
						|| (finArr[indices[i]] == 0 && srcArr[indices[i]] != 0)) {
					finArr[indices[i]] = finArr[indices[i]] + 1;
					tstSum = tstSum + 1;
					if (tstSum == sum) {
						break;
                    }
				}
            }
		}

		return finArr;
	}

	/**
	 * allocates values into an integer array so that the proportion of each
	 * element is as in a reference value and the sum of all elements equals to
	 * a specified value. Any negative values in the reference array will be
	 * ignored. For example, allocateProportionally(new int[] {1,5,2,-1,0},10)
	 * returns {1,6,3,-1,0}.
	 * 
	 * @param srcArr
	 *            the percentage of each of the elements in the array. The sum of this scrArr needs to be 1.
	 * @param sum
	 *            the value which the sum of the non-negative elements in the
	 *            final array must be equal to.
	 * @return an integer array satisfying the above conditions.
	 */
	public static int[] allocateProportionally(double[] srcArr, int sum) {
		int[] finArr = new int[srcArr.length];

		for (int i = 0; i <= finArr.length - 1; i++) {
			if (srcArr[i] < 0) {
				finArr[i] = -1;
            } else {
				finArr[i] = roundToCloserInt(srcArr[i]*sum);
            }
        }

		int tstSum = sumPositiveInArray(finArr);

		if (tstSum > sum) {
			int[] indices = sortedIndices(finArr);
			for (int i = indices.length - 1; i >= 0; i--) {
				if (finArr[indices[i]] > 0) {
					finArr[indices[i]] = finArr[indices[i]] - 1;
					tstSum = tstSum - 1;
					if (tstSum == sum)  {
						break;
                    }
				}
            }
		}
		if (tstSum < sum) {
			int[] indices = sortedIndices(finArr);
			for (int i = 0; i <= indices.length - 1; i++) {
				if (finArr[indices[i]] > 0
						|| (finArr[indices[i]] == 0 && srcArr[indices[i]] != 0)) {
					finArr[indices[i]] = finArr[indices[i]] + 1;
					tstSum = tstSum + 1;
					if (tstSum == sum) {
						break;
                    }
				}
            }
		}

		return finArr;
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
	 * gets the index of row and column of the last maximal value in a given
	 * matrix.
	 * 
	 * @param srcArray
	 *            the given matrix.
	 * @return index of row and column of the maximal value in the given matrix.
	 */
	public static int[] getIndexOfMax(int[][] srcArray) {
		int[] indices = new int[] { 0, 0 };
		for (int i = 0; i <= srcArray.length - 1; i++) {
			for (int j = 0; j <= srcArray[0].length - 1; j++) {
				if (srcArray[i][j] > srcArray[indices[0]][indices[1]]) {
					indices[0] = i;
					indices[1] = j;
				}
            }
        }
		return indices;
	}

	/**
	 * gets the minimal value in a given array.
	 * 
	 * @param srcArray
	 *            the input array
	 * @return minimal value in a given array.
	 */
	public static int min(int[] srcArray) {
		int imin = getIndexOfMin(srcArray);
		return srcArray[imin];
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
	 * @param map
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ) {
		 List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		 Collections.sort( list, new Comparator<Map.Entry<K, V>>()
				 {
			 public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			 {
				 return (o2.getValue()).compareTo( o1.getValue() );
			 }
				 } );

		 Map<K, V> result = new LinkedHashMap<K, V>();
		 for (Map.Entry<K, V> entry : list) {
			 result.put( entry.getKey(), entry.getValue() );
		 }
		 return result;
	 }
}
