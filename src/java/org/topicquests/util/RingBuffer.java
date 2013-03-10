/**
 * 
 */
package org.topicquests.util;
import java.util.*;
import org.topicquests.model.api.INode;
/**
 * @author park
 *
 */
public class RingBuffer {
	private ArrayList<INode> _buffer;
	private int maxsize = 0;
	private int last = 0;
	/**
	 * 
	 */
	public RingBuffer(int size) {
		_buffer = new ArrayList<INode>(size);
		maxsize = size;
	}

	public void add(INode n) {
		synchronized(_buffer) {
			if (!_buffer.contains(n)) {
				if (last > maxsize) {
					last--;
					_buffer.remove(maxsize-1);
				}
				last++;
				_buffer.add(0,n);
			}
		}
	}
	
	public List<INode> getBuffer() {
		synchronized(_buffer) {
			return (List<INode>)_buffer.clone();
		}
	}
	
	public int length() {
		return last;
	}
}
