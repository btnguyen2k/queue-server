/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.github.btnguyen2k.queueserver.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TQueueSizeResponse implements org.apache.thrift.TBase<TQueueSizeResponse, TQueueSizeResponse._Fields>, java.io.Serializable, Cloneable, Comparable<TQueueSizeResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TQueueSizeResponse");

  private static final org.apache.thrift.protocol.TField STATUS_FIELD_DESC = new org.apache.thrift.protocol.TField("status", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField MESSAGE_FIELD_DESC = new org.apache.thrift.protocol.TField("message", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField SIZE_FIELD_DESC = new org.apache.thrift.protocol.TField("size", org.apache.thrift.protocol.TType.I64, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TQueueSizeResponseStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TQueueSizeResponseTupleSchemeFactory());
  }

  public int status; // required
  public String message; // required
  public long size; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    STATUS((short)1, "status"),
    MESSAGE((short)2, "message"),
    SIZE((short)3, "size");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // STATUS
          return STATUS;
        case 2: // MESSAGE
          return MESSAGE;
        case 3: // SIZE
          return SIZE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __STATUS_ISSET_ID = 0;
  private static final int __SIZE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.STATUS, new org.apache.thrift.meta_data.FieldMetaData("status", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.MESSAGE, new org.apache.thrift.meta_data.FieldMetaData("message", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.SIZE, new org.apache.thrift.meta_data.FieldMetaData("size", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TQueueSizeResponse.class, metaDataMap);
  }

  public TQueueSizeResponse() {
  }

  public TQueueSizeResponse(
    int status,
    String message,
    long size)
  {
    this();
    this.status = status;
    setStatusIsSet(true);
    this.message = message;
    this.size = size;
    setSizeIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TQueueSizeResponse(TQueueSizeResponse other) {
    __isset_bitfield = other.__isset_bitfield;
    this.status = other.status;
    if (other.isSetMessage()) {
      this.message = other.message;
    }
    this.size = other.size;
  }

  public TQueueSizeResponse deepCopy() {
    return new TQueueSizeResponse(this);
  }

  @Override
  public void clear() {
    setStatusIsSet(false);
    this.status = 0;
    this.message = null;
    setSizeIsSet(false);
    this.size = 0;
  }

  public int getStatus() {
    return this.status;
  }

  public TQueueSizeResponse setStatus(int status) {
    this.status = status;
    setStatusIsSet(true);
    return this;
  }

  public void unsetStatus() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __STATUS_ISSET_ID);
  }

  /** Returns true if field status is set (has been assigned a value) and false otherwise */
  public boolean isSetStatus() {
    return EncodingUtils.testBit(__isset_bitfield, __STATUS_ISSET_ID);
  }

  public void setStatusIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __STATUS_ISSET_ID, value);
  }

  public String getMessage() {
    return this.message;
  }

  public TQueueSizeResponse setMessage(String message) {
    this.message = message;
    return this;
  }

  public void unsetMessage() {
    this.message = null;
  }

  /** Returns true if field message is set (has been assigned a value) and false otherwise */
  public boolean isSetMessage() {
    return this.message != null;
  }

  public void setMessageIsSet(boolean value) {
    if (!value) {
      this.message = null;
    }
  }

  public long getSize() {
    return this.size;
  }

  public TQueueSizeResponse setSize(long size) {
    this.size = size;
    setSizeIsSet(true);
    return this;
  }

  public void unsetSize() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SIZE_ISSET_ID);
  }

  /** Returns true if field size is set (has been assigned a value) and false otherwise */
  public boolean isSetSize() {
    return EncodingUtils.testBit(__isset_bitfield, __SIZE_ISSET_ID);
  }

  public void setSizeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SIZE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case STATUS:
      if (value == null) {
        unsetStatus();
      } else {
        setStatus((Integer)value);
      }
      break;

    case MESSAGE:
      if (value == null) {
        unsetMessage();
      } else {
        setMessage((String)value);
      }
      break;

    case SIZE:
      if (value == null) {
        unsetSize();
      } else {
        setSize((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case STATUS:
      return Integer.valueOf(getStatus());

    case MESSAGE:
      return getMessage();

    case SIZE:
      return Long.valueOf(getSize());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case STATUS:
      return isSetStatus();
    case MESSAGE:
      return isSetMessage();
    case SIZE:
      return isSetSize();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TQueueSizeResponse)
      return this.equals((TQueueSizeResponse)that);
    return false;
  }

  public boolean equals(TQueueSizeResponse that) {
    if (that == null)
      return false;

    boolean this_present_status = true;
    boolean that_present_status = true;
    if (this_present_status || that_present_status) {
      if (!(this_present_status && that_present_status))
        return false;
      if (this.status != that.status)
        return false;
    }

    boolean this_present_message = true && this.isSetMessage();
    boolean that_present_message = true && that.isSetMessage();
    if (this_present_message || that_present_message) {
      if (!(this_present_message && that_present_message))
        return false;
      if (!this.message.equals(that.message))
        return false;
    }

    boolean this_present_size = true;
    boolean that_present_size = true;
    if (this_present_size || that_present_size) {
      if (!(this_present_size && that_present_size))
        return false;
      if (this.size != that.size)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TQueueSizeResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetStatus()).compareTo(other.isSetStatus());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatus()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.status, other.status);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetMessage()).compareTo(other.isSetMessage());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMessage()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.message, other.message);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSize()).compareTo(other.isSetSize());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSize()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.size, other.size);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TQueueSizeResponse(");
    boolean first = true;

    sb.append("status:");
    sb.append(this.status);
    first = false;
    if (!first) sb.append(", ");
    sb.append("message:");
    if (this.message == null) {
      sb.append("null");
    } else {
      sb.append(this.message);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("size:");
    sb.append(this.size);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TQueueSizeResponseStandardSchemeFactory implements SchemeFactory {
    public TQueueSizeResponseStandardScheme getScheme() {
      return new TQueueSizeResponseStandardScheme();
    }
  }

  private static class TQueueSizeResponseStandardScheme extends StandardScheme<TQueueSizeResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TQueueSizeResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // STATUS
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.status = iprot.readI32();
              struct.setStatusIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // MESSAGE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.message = iprot.readString();
              struct.setMessageIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // SIZE
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.size = iprot.readI64();
              struct.setSizeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TQueueSizeResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(STATUS_FIELD_DESC);
      oprot.writeI32(struct.status);
      oprot.writeFieldEnd();
      if (struct.message != null) {
        oprot.writeFieldBegin(MESSAGE_FIELD_DESC);
        oprot.writeString(struct.message);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SIZE_FIELD_DESC);
      oprot.writeI64(struct.size);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TQueueSizeResponseTupleSchemeFactory implements SchemeFactory {
    public TQueueSizeResponseTupleScheme getScheme() {
      return new TQueueSizeResponseTupleScheme();
    }
  }

  private static class TQueueSizeResponseTupleScheme extends TupleScheme<TQueueSizeResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TQueueSizeResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetStatus()) {
        optionals.set(0);
      }
      if (struct.isSetMessage()) {
        optionals.set(1);
      }
      if (struct.isSetSize()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetStatus()) {
        oprot.writeI32(struct.status);
      }
      if (struct.isSetMessage()) {
        oprot.writeString(struct.message);
      }
      if (struct.isSetSize()) {
        oprot.writeI64(struct.size);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TQueueSizeResponse struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.status = iprot.readI32();
        struct.setStatusIsSet(true);
      }
      if (incoming.get(1)) {
        struct.message = iprot.readString();
        struct.setMessageIsSet(true);
      }
      if (incoming.get(2)) {
        struct.size = iprot.readI64();
        struct.setSizeIsSet(true);
      }
    }
  }

}

