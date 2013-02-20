/* Timer is used to measure the response time of the servers. */

package com.jackson;

public class Timer {
	private long start;
	private long stop;

	public Timer(){
		reset();
		}

	public void start(){
		start = System.nanoTime();
		}

	public void stop(){
		stop = System.nanoTime();
		}

	/* if i use float, it returns 0.0 times.  if i cast to float,
	 * it returns the correct time as a float. */
	public double duration(){
		return (stop-start)/1000000.0;
	  	}

	public void reset(){
		start = 0;  
		stop = 0;
		}
}
