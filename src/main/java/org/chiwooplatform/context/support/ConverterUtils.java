package org.chiwooplatform.context.support;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;

import org.springframework.beans.BeanUtils;

import org.chiwooplatform.context.Constants;

/**
 * @author aider
 */
public final class ConverterUtils {

    /**
     * value 값이 없다면 defaultValue 값을 리턴 한다.
     *
     * @param value the value to cast as a String.
     * @param defaultValue replacement value if the value is not valid.
     * @return a value of string type
     */
    public static String nvl( String value, String defaultValue ) {
        if ( value != null ) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * value 값이 없다면 defaultValue 값을 리턴 한다.
     *
     * @param value the value to cast as a Integer.
     * @param defaultValue replacement value if the value is not valid.
     * @return a value of integer type
     */
    public static Integer nvl( Integer value, Integer defaultValue ) {
        if ( value != null ) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * value 파라미터 값을 integer 객체의 값으로 변환한 값을 리턴
     *
     * @param value the value to cast as a Integer.
     * @return a value of integer type
     */
    public static Integer getInteger( Object value ) {
        if ( value == null ) {
            return null;
        }
        if ( value instanceof Integer ) {
            return (Integer) value;
        }
        if ( value instanceof String ) {
            return Integer.valueOf( (String) value );
        } else if ( value instanceof BigDecimal ) {
            return ( (BigDecimal) value ).intValue();
        } else if ( value instanceof BigInteger ) {
            return ( (BigInteger) value ).intValue();
        } else if ( value instanceof Long ) {
            return ( (Long) value ).intValue();
        }
        return (Integer) value;
    }

    /**
     * value 파라미터 값을 Long 객체의 값으로 변환한 값을 리턴
     *
     * @param value the value to cast as a Long.
     * @return a value of long type
     */
    public static Long getLong( Object value ) {
        if ( value == null ) {
            return null;
        }
        if ( value instanceof Integer ) {
            return (Long) value;
        } else if ( value instanceof BigDecimal ) {
            return ( (BigDecimal) value ).longValue();
        } else if ( value instanceof BigInteger ) {
            return ( (BigInteger) value ).longValue();
        } else if ( value instanceof Long ) {
            return ( (Long) value ).longValue();
        }
        return (Long) value;
    }

    /**
     * @param value the value to cast as a String[].
     * @return delimiterValues
     */
    public static String[] getDelimiterValues( final String value ) {
        if ( value == null ) {
            return new String[] { "0" };
        }
        StringTokenizer token = new StringTokenizer( value, Constants.DEFAULT_DELIMITER );
        LinkedList<String> set = new LinkedList<>();
        while ( token.hasMoreTokens() ) {
            set.add( token.nextToken() );
        }
        if ( set.size() < 1 ) {
            return new String[] { value };
        }
        String[] arrays = set.toArray( new String[set.size()] );
        return arrays;
    }

    /**
     * @param values String collection like array, map, list.
     * @return a string value that contains concatenated a comma "," (ex: key1, key2, key3...).
     */
    public static String toCommaDelimitedString( String... values ) {
        if ( values == null ) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for ( String val : values ) {
            if ( index == 0 ) {
                builder.append( val );
            } else {
                builder.append( ", " ).append( val );
            }
            ++index;
        }
        return builder.toString();
    }

    /**
     * Rebind the data type for Boolean properties.
     *
     * @param params map parameter
     * @param properties Boolean attribute name
     */
    public static void rebindBooleanProperties( Map<String, Object> params, String... properties ) {
        for ( String prop : properties ) {
            String value = (String) params.get( prop );
            if ( "true".equals( value ) || "false".equals( value ) ) {
                params.put( prop, Boolean.valueOf( value ) );
            }
        }
    }

    /**
     * Rebind the data type for Boolean properties.
     *
     * @param params 파라미터 맵
     */
    public static void rebindBooleanProperties( Map<String, Object> params ) {
        if ( params != null ) {
            for ( Map.Entry<String, Object> p : params.entrySet() ) {
                String name = p.getKey();
                if ( name.endsWith( "_yn" ) ) {
                    String value = (String) p.getValue();
                    if ( "true".equals( value ) || "false".equals( value ) ) {
                        params.put( name, Boolean.valueOf( value ) );
                    }
                }
            }
        }
    }

    /**
     * @param name Property name to ignore.
     * @param ignoreProperties Match names to ignore
     * @return Boolean
     */
    static private boolean ignoreProperty( String name, String... ignoreProperties ) {
        for ( String propName : ignoreProperties ) {
            if ( name.equals( propName ) ) {
                // System.out.println( "ignoreProperty ----- " + name );
                return true;
            }
        }
        return false;
    }

    /**
     * Java 오브젝트 객체를 파라미터로, Map&#60;K, V&#62; 컬렉션 맵으로 변환 한다.
     * 
     * @param bean 소스 객체
     * @return 컬렉션 맵 java.util.Map
     * @throws Exception error occurred while converting to a map object.
     */
    static public Map<String, Object> toMap( Object bean )
        throws Exception {
        return toMap( bean, "class", "blob_body" );
    }

    /**
     * Java 오브젝트 객체를 파라미터로, Map&#60;K, V&#62; 컬렉션 맵으로 변환 한다.
     * 
     * @param bean 소스 객체
     * @param ignoreProperties 무시할 속성
     * @return 컬렉션 맵 java.util.Map
     * @throws Exception error occurred while converting to a map object.
     */
    static public Map<String, Object> toMap( Object bean, String... ignoreProperties )
        throws Exception {
        if ( bean == null ) {
            return null;
        }
        if ( ignoreProperties == null ) {
            ignoreProperties = new String[] { "class", "blob_body" };
        }
        Map<String, Object> row = new HashMap<>();
        try {
            PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors( bean.getClass() );
            for ( PropertyDescriptor descriptor : descriptors ) {
                String name = descriptor.getName();
                if ( ignoreProperty( name, ignoreProperties ) ) {
                    continue;
                }
                Method getter = descriptor.getReadMethod();
                if ( getter == null ) {
                    continue;
                }
                String getterName = getter.getName();
                Object value = getter.invoke( bean, new Object[] {} );
                if ( getterName.startsWith( "get" ) || getterName.startsWith( "is" ) ) {
                    row.put( name, value );
                }
            }
            return row;
        } catch ( Exception e ) {
            throw e;
        }
    }

    static private void setterDouble( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( paramType == Integer.class ) {
            setter.invoke( bean, new Double( (Integer) value ) );
        } else if ( paramType == Float.class ) {
            setter.invoke( bean, ( (Float) value ).doubleValue() );
        } else if ( paramType == Long.class ) {
            setter.invoke( bean, ( (Long) value ).doubleValue() );
        } else if ( paramType == BigDecimal.class ) {
            setter.invoke( bean, ( (BigDecimal) value ).doubleValue() );
        } else if ( paramType == BigInteger.class ) {
            setter.invoke( bean, ( (BigInteger) value ).doubleValue() );
        } else if ( paramType == String.class ) {
            setter.invoke( bean, Double.parseDouble( (String) value ) );
        } else {
            setter.invoke( bean, value );
        }
    }

    static private void setterInteger( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( paramType == Double.class ) {
            setter.invoke( bean, ( (Double) value ).intValue() );
        } else if ( paramType == Float.class ) {
            setter.invoke( bean, ( (Float) value ).intValue() );
        } else if ( paramType == Long.class ) {
            setter.invoke( bean, ( (Long) value ).intValue() );
        } else if ( paramType == BigDecimal.class ) {
            setter.invoke( bean, ( (BigDecimal) value ).intValue() );
        } else if ( paramType == BigInteger.class ) {
            setter.invoke( bean, ( (BigInteger) value ).intValue() );
        } else if ( paramType == String.class ) {
            setter.invoke( bean, Integer.parseInt( (String) value ) );
        } else if ( paramType == Boolean.class ) {
            if ( ( (Boolean) value ) ) {
                setter.invoke( bean, 1 );
            } else {
                setter.invoke( bean, 0 );
            }
        } else {
            setter.invoke( bean, value );
        }
    }

    static private void setterFloat( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( paramType == Double.class ) {
            setter.invoke( bean, ( (Double) value ).floatValue() );
        } else if ( paramType == Integer.class ) {
            setter.invoke( bean, ( (Integer) value ).floatValue() );
        } else if ( paramType == Long.class ) {
            setter.invoke( bean, ( (Long) value ).floatValue() );
        } else if ( paramType == BigDecimal.class ) {
            setter.invoke( bean, ( (BigDecimal) value ).floatValue() );
        } else if ( paramType == BigInteger.class ) {
            setter.invoke( bean, ( (BigInteger) value ).floatValue() );
        } else if ( paramType == String.class ) {
            setter.invoke( bean, Float.parseFloat( (String) value ) );
        } else {
            setter.invoke( bean, value );
        }
    }

    static private void setterLong( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( paramType == Integer.class ) {
            setter.invoke( bean, ( (Integer) value ).longValue() );
        } else if ( paramType == Double.class ) {
            setter.invoke( bean, ( (Double) value ).longValue() );
        } else if ( paramType == Float.class ) {
            setter.invoke( bean, ( (Float) value ).longValue() );
        } else if ( paramType == BigDecimal.class ) {
            setter.invoke( bean, ( (BigDecimal) value ).longValue() );
        } else if ( paramType == BigInteger.class ) {
            setter.invoke( bean, ( (BigInteger) value ).longValue() );
        } else if ( paramType == String.class ) {
            setter.invoke( bean, Long.parseLong( (String) value ) );
        } else {
            setter.invoke( bean, value );
        }
    }

    static private void setterBoolean( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int v = 0;
        if ( paramType == Integer.class ) {
            v = ( (Integer) value ).intValue();
        } else if ( paramType == Double.class ) {
            v = ( (Double) value ).intValue();
        } else if ( paramType == Float.class ) {
            v = ( (Float) value ).intValue();
        } else if ( paramType == BigDecimal.class ) {
            v = ( (BigDecimal) value ).intValue();
        } else if ( paramType == BigInteger.class ) {
            v = ( (BigInteger) value ).intValue();
        } else if ( paramType == String.class ) {
            setter.invoke( bean, Boolean.getBoolean( (String) value ) );
        } else {
            setter.invoke( bean, Boolean.getBoolean( (String) value ) );
        }
        if ( v == 1 ) {
            setter.invoke( bean, Boolean.TRUE );
        } else {
            setter.invoke( bean, Boolean.FALSE );
        }
    }

    static private void setterDate( Method setter, Object bean, Class<?> paramType, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if ( paramType == Timestamp.class ) {
            setter.invoke( bean, new Date( ( (Timestamp) value ).getTime() ) );
        } else if ( paramType == Long.class ) {
            setter.invoke( bean, new Date( (Long) value ) );
        }
        //        else if ( paramType == Double.class )
        //        {
        //            ( (Double) value ).longValue();
        //        }
        //        else if ( paramType == Float.class )
        //        {
        //            ( (Float) value ).longValue();
        //        }
        //        else if ( paramType == BigDecimal.class )
        //        {
        //            ( (BigDecimal) value ).longValue();
        //        }
        //        else if ( paramType == BigInteger.class )
        //        {
        //            ( (BigInteger) value ).longValue();
        //        }
        //        else if ( paramType == String.class )
        //        {
        //            Long.parseLong( (String) value );
        //        }
        else {
            setter.invoke( bean, new Date( Long.parseLong( (String) value ) ) );
        }
    }

    static public void setProperty( Object source, Object target )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        setProperty( target.getClass().getSimpleName(), source, target );
    }

    static public void setProperty( final String propertyName, Object source, Object value )
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // ReflectionTestUtils
        PropertyDescriptor desc = BeanUtils.getPropertyDescriptor( source.getClass(), propertyName );
        Method setter = desc.getWriteMethod();
        setter.invoke( source, value );
    }

    static private boolean isUpperCase( char c ) {
        return c >= 'A' && c <= 'Z';
    }

    static private String toSnakeCase( final String attrName ) {
        StringBuilder builder = new StringBuilder();
        boolean wasUpperCase = false;
        for ( int i = 0; i < attrName.length(); i++ ) {
            char c = attrName.charAt( i );
            if ( i != 0 ) {
                boolean isUpperCase = isUpperCase( c );
                if ( isUpperCase && !wasUpperCase ) {
                    builder.append( '_' );
                }
                wasUpperCase = isUpperCase;
            }
            builder.append( c );
        }
        return builder.toString().toLowerCase();
    }

    /**
     * Map&#60;K, V&#62; 맵 컬렉션 객체를 파라미터로 선언된 클래스 타입 &#62;T&#60; 로 변환 한다.
     *
     * @param row Map&#60;K, V&#62;
     * @param <T> Java 객체로 변환할 클래스 타입
     * @param clazz T Java 객체로 변환할 클래스 타입
     * @return PoJo 객체
     * @throws Exception error occurred while converting to a PoJo object.
     */
    static public <T> T toBean( Map<String, Object> row, Class<T> clazz )
        throws Exception {
        if ( row == null ) {
            throw new RuntimeException( "Can not convert to target class. Source object is null" );
        }
        try {
            T bean = clazz.newInstance();
            PropertyDescriptor[] props = BeanUtils.getPropertyDescriptors( clazz );
            for ( PropertyDescriptor desc : props ) {
                final String name = desc.getName();
                Object value = row.get( name );
                if ( value == null ) {
                    value = row.get( toSnakeCase( name ) );
                }
                // System.out.printf( "\nname : %s   snakeCase: %s    value: %s", name, toSnakeCase( name ), value );
                Method setter = desc.getWriteMethod();
                if ( value != null && setter != null ) {
                    Class<?> valueType = value.getClass();
                    Class<?> parameterType = setter.getParameterTypes()[0];
                    //  System.out.printf( "==========\n name : %s valueType: %s value: %s, setterType: %s\n", name, valueType.getTypeName(), value.toString(),
                    //      parameterType.getTypeName() );
                    if ( valueType == parameterType ) {
                        setter.invoke( bean, value );
                    } else {
                        if ( parameterType == String.class ) {
                            setter.invoke( bean, (String) value );
                        } else if ( parameterType == Double.class ) {
                            setterDouble( setter, bean, valueType, value );
                        } else if ( parameterType == Integer.class ) {
                            setterInteger( setter, bean, valueType, value );
                        } else if ( parameterType == Float.class ) {
                            setterFloat( setter, bean, valueType, value );
                        } else if ( parameterType == Long.class ) {
                            setterLong( setter, bean, valueType, value );
                        } else if ( parameterType == Boolean.class ) {
                            setterBoolean( setter, bean, valueType, value );
                        } else if ( parameterType == Date.class ) {
                            setterDate( setter, bean, valueType, value );
                        } else {
                            setter.invoke( bean, value );
                        }
                    }
                }
            }
            return bean;
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
            //throw new IllegalArgumentException( e );
        }
    }

    /**
     * List&#60;Map&#60;K, V&#62;&#62; 리스트 컬렉션 객체를 파라미터로 선언된 클래스 타입 &#60;T&#62;를 통해 리스트 컬렉션 List&#60;T&#62;로 변환 한다.
     *
     * @param rows List&#60;Map&#60;K, V&#62;&#62; 컬렉션
     * @param <M> 변환할 클래스 타입
     * @param <T> 변환될 클래스 타입
     * @param clazz 변환될 클래스 타입
     * @return 컬렉션 객체
     * @throws Exception error occurred while converting to a Collection object that contains Map.
     */
    static public <M extends Map<String, Object>, T> List<T> toBeans( List<M> rows, Class<T> clazz )
        throws Exception {
        if ( rows == null ) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for ( M row : rows ) {
            T bean = toBean( row, clazz );
            list.add( bean );
        }
        return list;
    }

    /**
     * Sets the attributes of the source object to target class.
     * 
     * @param source 소스 오브젝트 객체
     * @param clazz 리턴 오브젝트 타입
     * @param <T> is return type.
     * @return 리턴 오브젝트
     * @throws Exception an error occurred while binding attributes from a source object.
     */
    static public <T> T copyProperties( Object source, Class<T> clazz )
        throws Exception {
        if ( source == null ) {
            return null;
        }
        T target = BeanUtils.instantiate( clazz );
        BeanUtils.copyProperties( source, target );
        return target;
    }

    /**
     * source 객체를 타겟 clazz 객체로 복제 하고, 복제된 타겟 객체를 리턴 한다.
     * 
     * @param source 소스 오브젝트 객체
     * @param clazz 리턴 오브젝트 타입
     * @param <T> is return type.
     * @param ignoreProperties 무시될 속성 명 배열
     * @return 리턴 오브젝트
     * @throws Exception an error occurred while binding attributes from a source object.
     */
    static public <T> T copyProperties( Object source, Class<T> clazz, String... ignoreProperties )
        throws Exception {
        if ( source == null ) {
            return null;
        }
        T target = BeanUtils.instantiate( clazz );
        BeanUtils.copyProperties( source, target, ignoreProperties );
        return target;
    }

    /**
     * Copies the attributes of the source object to the attributes of the target object.
     * 
     * @param source source object.
     * @param target target object.
     * @throws Exception an error occurred while binding attributes from a source object.
     */
    static public void copyProperties( Object source, Object target )
        throws Exception {
        if ( source == null || target == null ) {
            throw new RuntimeException( "Source or Target object is null." );
        }
        BeanUtils.copyProperties( source, target );
    }

    private final static char BL = ' ';

    static public String mapToString( Map<String, Object> source ) {
        if ( source == null ) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        ret.append( "{" );
        Iterator<Entry<String, Object>> iter = source.entrySet().iterator();
        while ( iter.hasNext() ) {
            ret.append( BL );
            Entry<String, ?> entry = iter.next();
            ret.append( entry.getKey() );
            ret.append( '=' ).append( '"' );
            Object v = entry.getValue();
            if ( v == null ) {
                ret.append( "{null}" );
            } else {
                if ( v instanceof String ) {
                    ret.append( v );
                } else {
                    ret.append( v.toString() );
                }
            }
            ret.append( '"' );
            if ( iter.hasNext() ) {
                ret.append( ',' );
            }
        }
        ret.append( BL );
        ret.append( "}" );
        return ret.toString();
    }
}
