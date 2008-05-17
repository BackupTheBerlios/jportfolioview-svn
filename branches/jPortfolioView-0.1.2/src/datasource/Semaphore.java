/*
* Copyright (C) 2008 Andreas Reichel
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package datasource;

public class Semaphore {
    private static Semaphore instance;
    private int cnt = 1;
    private static final int maxcnt=3;
    
    public Semaphore( int cnt ) {
	if ( cnt > 1 )  // nur Z�hler gr��er 1 akzeptieren
	    this.cnt = cnt;
    }
    
    public static Semaphore getInstance() {
	if (instance == null) {
	    instance = new Semaphore(3);
	}
	return instance;
    }
    
    public synchronized void enter()  {
	while ( cnt <= 0 )
	    try {
		wait(2000);
	    } catch( InterruptedException e ) {
	    }
	cnt--;
	System.out.println("Threads: " + cnt);
    }
    
    public synchronized void leave()  {
	cnt++;
	if ( cnt > 0 ) notifyAll();
	System.out.println("Threads: " + cnt);
    }
    
    public void setLimit(int cnt) {
	this.cnt=cnt;
    }
}

