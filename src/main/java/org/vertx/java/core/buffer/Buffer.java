/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vertx.java.core.buffer;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.buffer.DynamicChannelBuffer;
import org.jboss.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * <p>A Buffer represents a sequence of zero or more bytes that can be written to or read from, and which expands as necessary to accomodate any bytes written to it.</p>
 *
 * <p>Buffer instances should always be created using the static factory methods that take the form {@code createXXX}.
 * Factory methods exist for creating Buffer instances from a {@code byte[]} and a {@code String}.</p>
 *
 * <p>There are two ways to write data to a Buffer: The first method involves methods that take the form {@code setXXX}.
 * These methods write data into the buffer starting at the specified position. The position does not have to be inside data that
 * has already been written to the buffer; the buffer will automatically expand to encompass the position plus any data that needs
 * to be written. All positions are measured in bytes and start with zero.</p>
 *
 * <p>The second method involves methods that take the form {@code appendXXX}; these methods append data
 * at the end of the buffer.</p>
 *
 * <p>Methods exist to both {@code set} and {@code append} all primitive types, {@link java.lang.String}, {@link java.nio.ByteBuffer} and
 * other instances of Buffer.</p>
 *
 * <p>Data can be read from a buffer by invoking methods which take the form {@code getXXX}. These methods take a parameter
 * representing the position in the Buffer from where to read data.</p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class Buffer {

  //vert.x buffers are always dynamic
  private DynamicChannelBuffer buffer;

  /**
   * Creates a new empty Buffer that is expected to have a size of {@code initialSizeHint} after data has been
   * written to it.<p> Please note that {@code length} of the Buffer immediately after creation will be zero. The {@code initialSizeHint}
   * is merely a hint to the system for how much memory to initially allocate to the buffer to prevent excessive
   * automatic re-allocations as data is written to it.
   */
  public static Buffer create(int initialSizeHint) {
    return new Buffer(ChannelBuffers.dynamicBuffer(initialSizeHint));
  }

  /**
   * Create a new Buffer that contains the contents of the {@code byte[] bytes}
   */
  public static Buffer create(byte[] bytes) {
    return new Buffer(ChannelBuffers.wrappedBuffer(bytes));
  }

  /**
   * Create a new Buffer that contains the contents of {@code String str} encoded according to the encoding {@code enc}
   */
  public static Buffer create(String str, String enc) {
    return new Buffer(ChannelBuffers.copiedBuffer(str, Charset.forName(enc)));
  }

  /**
   * Create a new Buffer that contains the contents of {@code String str} encoded with UTF-8 encoding
   */
  public static Buffer create(String str) {
    return Buffer.create(str, "UTF-8");
  }

  /**
   * Create a new Buffer from a Netty {@code ChannelBuffer} instance. Please use the static {@code createXXX methods}
   * to create Buffer instances.<p>
   * This method is meant for internal use only.
   */
  public Buffer(ChannelBuffer buffer) {
    if (buffer instanceof DynamicChannelBuffer) {
      this.buffer = (DynamicChannelBuffer) buffer;
    } else {
      //TODO - if Netty could provide a DynamicChannelBuffer constructor which took a HeapBuffer this would
      //save an extra copy
      this.buffer = (DynamicChannelBuffer) ChannelBuffers.dynamicBuffer(buffer.readableBytes());
      this.buffer.writeBytes(buffer, 0, buffer.readableBytes());
    }
  }

  /**
   * Returns a {@code String} represention of the Buffer assuming it contains a {@code String} encoding in UTF-8
   */
  public String toString() {
    return buffer.toString(Charset.forName("UTF-8"));
  }

  /**
   * Returns a {@code String} represention of the Buffer with the encoding specified by {@code enc}
   */
  public String toString(String enc) {
    return buffer.toString(Charset.forName(enc));
  }

  /**
   * Returns the {@code byte} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 1} is greater than the length {@code } of the Buffer.
   */
  public byte getByte(int pos) {
    return buffer.getByte(pos);
  }

  /**
   * Returns the {@code int} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 4} is greater than the length {@code } of the Buffer.
   */
  public int getInt(int pos) {
    return buffer.getInt(pos);
  }

  /**
   * Returns the {@code long} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 8} is greater than the length {@code } of the Buffer.
   */
  public long getLong(int pos) {
    return buffer.getLong(pos);
  }

  /**
   * Returns the {@code double} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 8} is greater than the length {@code } of the Buffer.
   */
  public double getDouble(int pos) {
    return buffer.getDouble(pos);
  }

  /**
   * Returns the {@code float} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 4} is greater than the length {@code } of the Buffer.
   */
  public float getFloat(int pos) {
    return buffer.getFloat(pos);
  }

  /**
   * Returns the {@code short} at position {@code pos} in the Buffer.
   *
   * @throws IndexOutOfBoundsException if the specified {@code pos} is less than {@code 0} or
   *                                   {@code pos + 2} is greater than the length {@code } of the Buffer.
   */
  public short getShort(int pos) {
    return buffer.getShort(pos);
  }

  /**
   * Returns a copy of the entire Buffer as a {@code byte[]}
   */
  public byte[] getBytes() {
    byte[] arr = new byte[buffer.writerIndex()];
    buffer.getBytes(0, arr);
    return arr;
  }

  /**
   * Returns a copy of a sub-sequence the Buffer as a {@code byte[]} starting at position {@code start}
   * and ending at position {@code end - 1}
   */
  public byte[] getBytes(int start, int end) {
    byte[] arr = new byte[end - start];
    buffer.getBytes(start, arr, 0, end - start);
    return arr;
  }

  /**
   * Appends the specified {@code Buffer} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendBuffer(Buffer buff) {
    ChannelBuffer cb = buff.getChannelBuffer();
    buffer.writeBytes(buff.getChannelBuffer());
    cb.readerIndex(0); // Need to reset readerindex since Netty write modifies readerIndex of source!
    return this;
  }

  /**
   * Appends the specified {@code byte[]} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendBytes(byte[] bytes) {
    buffer.writeBytes(bytes);
    return this;
  }

  /**
   * Appends the specified {@code byte} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendByte(byte b) {
    buffer.writeByte(b);
    return this;
  }

  /**
   * Appends the specified {@code int} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendInt(int i) {
    buffer.writeInt(i);
    return this;
  }

  /**
   * Appends the specified {@code long} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendLong(long l) {
    buffer.writeLong(l);
    return this;
  }

  /**
   * Appends the specified {@code short} to the end of the Buffer.The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendShort(short s) {
    buffer.writeShort(s);
    return this;
  }

  /**
   * Appends the specified {@code float} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendFloat(float f) {
    buffer.writeFloat(f);
    return this;
  }

  /**
   * Appends the specified {@code double} to the end of the Buffer. The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.
   */
  public Buffer appendDouble(double d) {
    buffer.writeDouble(d);
    return this;
  }

  /**
   * Appends the specified {@code String str} to the end of the Buffer with the encoding as specified by {@code enc}.<p>
   * The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together.<p>
   */
  public Buffer appendString(String str, String enc) {
    return append(str, Charset.forName(enc));
  }

  /**
   * Appends the specified {@code String str} to the end of the Buffer with UTF-8 encoding.<p>
   * The buffer will expand as necessary to accomodate any bytes written.<p>
   * Returns a reference to {@code this} so multiple operations can be appended together<p>
   */
  public Buffer appendString(String str) {
    return append(str, CharsetUtil.UTF_8);
  }

  /**
   * Sets the {@code byte} at position {@code pos} in the Buffer to the value {@code b}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setByte(int pos, byte b) {
    ensureWritable(pos, 1);
    buffer.setByte(pos, b);
    return this;
  }

  /**
   * Sets the {@code int} at position {@code pos} in the Buffer to the value {@code i}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setInt(int pos, int i) {
    ensureWritable(pos, 4);
    buffer.setInt(pos, i);
    return this;
  }

  /**
   * Sets the {@code long} at position {@code pos} in the Buffer to the value {@code i}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setLong(int pos, long l) {
    ensureWritable(pos, 8);
    buffer.setLong(pos, l);
    return this;
  }

  /**
   * Sets the {@code double} at position {@code pos} in the Buffer to the value {@code i}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setDouble(int pos, double d) {
    ensureWritable(pos, 8);
    buffer.setDouble(pos, d);
    return this;
  }

  /**
   * Sets the {@code float} at position {@code pos} in the Buffer to the value {@code i}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setFloat(int pos, float f) {
    ensureWritable(pos, 4);
    buffer.setFloat(pos, f);
    return this;
  }

  /**
   * Sets the {@code short} at position {@code pos} in the Buffer to the value {@code i}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setShort(int pos, short s) {
    ensureWritable(pos, 2);
    buffer.setShort(pos, s);
    return this;
  }

  /**
   * Sets the bytes at position {@code pos} in the Buffer to the value {@code b}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setBuffer(int pos, Buffer b) {
    ensureWritable(pos, b.length());
    buffer.setBytes(pos, b.getChannelBuffer());
    return this;
  }

  /**
   * Sets the bytes at position {@code pos} in the Buffer to the value {@code b}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setBytes(int pos, ByteBuffer b) {
    ensureWritable(pos, b.limit());
    buffer.setBytes(pos, b);
    return this;
  }

  /**
   * Sets the bytes at position {@code pos} in the Buffer to the value {@code b}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setBytes(int pos, byte[] b) {
    ensureWritable(pos, b.length);
    buffer.setBytes(pos, b);
    return this;
  }

  /**
   * Sets the bytes at position {@code pos} in the Buffer to the value of {@code str} endoded in UTF-8.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setBytes(int pos, String str) {
    return setBytes(pos, str, CharsetUtil.UTF_8);
  }

  /**
   * Sets the bytes at position {@code pos} in the Buffer to the value of {@code str} encoded in encoding {@code enc}.<p>
   * The buffer will expand as necessary to accomodate any value written.
   */
  public Buffer setBytes(int pos, String str, String enc) {
    return setBytes(pos, str, Charset.forName(enc));
  }

  /**
   * Returns the length of the buffer, measured in bytes. The length is defined as the largest position of any byte in the
   * buffer + 1. All positions are indexed from zero.
   */
  public int length() {
    return buffer.writerIndex();
  }

  /**
   * Returns a copy of a sub-sequence the Buffer starting at position {@code start}
   * and ending at position {@code end - 1}.
   */
  public Buffer copy(int start, int end) {
    return new Buffer(buffer.copy(start, end - start));
  }

  /**
   * Returns a copy of the entire Buffer.
   */
  public Buffer copy() {
    return new Buffer(buffer.copy());
  }

  /**
   * Returns the Buffer as a Netty {@code ChannelBuffer}.<p>
   * This method is meant for internal use only.
   */
  public ChannelBuffer getChannelBuffer() {
    return buffer;
  }

  private Buffer append(String str, Charset charset) {
    byte[] bytes = str.getBytes(charset);
    buffer.writeBytes(bytes);
    return this;
  }

  private Buffer setBytes(int pos, String str, Charset charset) {
    byte[] bytes = str.getBytes(charset);
    ensureWritable(pos, bytes.length);
    buffer.setBytes(pos, bytes);
    return this;
  }

  //TODO this is all a bit of a pain - if we can just throw exceptions if people set stuff outside of the buffer
  //like Netty that would be preferable
  private void ensureWritable(int pos, int len) {
    int ni = pos + len;
    int cap = buffer.capacity();
    int over = ni - cap;
    if (over > 0) {
      buffer.writerIndex(cap);
      buffer.ensureWritableBytes(over);
    }
    //We have to make sure that the writerindex is always positioned on the last bit of data set in the buffer
    if (ni > buffer.writerIndex()) {
      buffer.writerIndex(ni);
    }
  }

}
