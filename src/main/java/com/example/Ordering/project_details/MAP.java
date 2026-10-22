package com.example.Ordering.project_details;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Krishana dubey
 */
public class MAP {

    public static <T1,T2> T2 map(T1 obj1, Supplier<T2> obj, String ...o ){

        T2 obj2 =  obj.get();
        Set<String> strings = emptyFunction(
                obj1, obj2, Arrays.stream(o).
                        collect(Collectors.toSet())
        );
        BeanUtils.copyProperties(obj1,obj2,strings.toArray(new String[]{}));

        // localDate identify them and parse them into string
        Set<PropertyDescriptor> LocalDateAttributes = localDatePropertyNameGive(obj1,obj2,strings);
        try {
            execute(LocalDateAttributes,obj1,obj2);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return obj2;
    }
    private static<T1,T2> void execute(Set<PropertyDescriptor> localDate , T1 obj1, T2 obj2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (PropertyDescriptor descriptor :localDate) {
          if(descriptor.getReadMethod().getDeclaringClass().getName().equals(obj1.getClass().getName())){
              System.out.println(descriptor.getReadMethod().getName());
              Method method1 = obj1.getClass().getMethod(descriptor.getReadMethod().getName());
              String invoke =method1.invoke(obj1).toString();

              Method method = obj2.getClass().getMethod(descriptor.getWriteMethod().getName(),String.class);
            method.invoke(obj2,invoke);
          }else{
              Method method1 = obj1.getClass().getMethod(descriptor.getReadMethod().getName());
              String invoke =(String)(method1.invoke(obj1));
                  Method method = obj2.getClass().getMethod(descriptor.getWriteMethod().getName(),descriptor.getPropertyType());
                   if(descriptor.getPropertyType().getName().equals(LocalDate.class.getName()))
                    method.invoke(obj2,LocalDate.parse(invoke ,DateTimeFormatter.ofPattern("MM-dd-yyyy")));
             else if(descriptor.getPropertyType().getName().equals(LocalDateTime.class.getName()))
                  method.invoke(obj2,LocalDateTime.parse(invoke,DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
             else if(descriptor.getPropertyType().getName().equals(Date.class.getName()))
                  method.invoke(obj2,Date.parse(invoke));
             else if(descriptor.getPropertyType().getName().equals(LocalTime.class.getName()))
                 method.invoke(obj2,LocalTime.parse(invoke,DateTimeFormatter.ofPattern("HH:mm:ss")));

          }
        }
    }
    private static <T1,T2>Set<PropertyDescriptor> localDatePropertyNameGive(T1 obj1, T2 obj2,Set<String> st){
        BeanWrapper src2 = PropertyAccessorFactory.forBeanPropertyAccess(obj1);
        PropertyDescriptor[]  propertyDescriptors2 = src2.getPropertyDescriptors();
        Set<PropertyDescriptor> empty = new HashSet<>();
        addLocalDate(empty, propertyDescriptors2,st);

        BeanWrapper src1 = PropertyAccessorFactory.forBeanPropertyAccess(obj2);
        PropertyDescriptor[]  propertyDescriptors1 = src1.getPropertyDescriptors();

        addLocalDate(empty, propertyDescriptors1,st);
        return empty;
    }

    private static void addLocalDate(Set<PropertyDescriptor> empty, PropertyDescriptor[] propertyDescriptors1,Set<String> set) {
        for(PropertyDescriptor t:propertyDescriptors1){
            String type= t.getPropertyType().getTypeName();
            if(!set.contains(type) && (type.equals(LocalDate.class.getName()) || type.equals(Date.class.getName()) || type.equals(LocalDateTime.class.getName()) || type.equals(LocalTime.class.getName()))){
                empty.add(t);
            }
        }
    }

    public static <T1,T2> void copyInTheObject(T1 obj1, T2 obj2, String ...o ){
        Set<String> p = emptyFunction(
                obj1,obj2,
                Arrays.stream(o).
                        collect(Collectors.toSet())
        );
        BeanUtils.copyProperties(obj1,obj2);

        // localDate identify them and parse them into string
        Set<PropertyDescriptor> LocalDateAttributes = localDatePropertyNameGive(obj1,obj2,p);
        try {
            execute(LocalDateAttributes,obj1,obj2);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
    private static <T1,T2>Set<String> emptyFunction(T1 obj1,T2 obj2 , Set<String> empty){
       BeanWrapper src = PropertyAccessorFactory.forBeanPropertyAccess(obj1);
       PropertyDescriptor[]  propertyDescriptors = src.getPropertyDescriptors();
        // storing all the methods for removing the missing methods in obj2 with obj1


      for(PropertyDescriptor val:propertyDescriptors){
          // since passwordEncoder is not able to give its value
          // so manually added it bcz it throws a ClassNotFoundError
          String type= val.getPropertyType().getTypeName();
          String name = val.getName();
          if(name.equals("passwordEncoder")){
              empty.add(name);
              continue;
          }
        if( src.getPropertyValue(name) == null){
            empty.add(name);
        }
      }

        return empty;
    }




     public static <T1,T2> T2 mapRequired(T1 obj1, Supplier<T2> obj, String ...o ){
       T2 obj2 =  obj.get();
        BeanUtils.copyProperties(obj1,obj2,emptyFunction(
                        obj1,obj2
                ,       Arrays.stream(o).
                        collect(Collectors.toSet())
        ).toArray(new String[]{}));
        return obj2;
    }
    public static <T1,T2> void copyInTheObjectRequired(T1 obj1, T2 obj2, String ...o ){
        Set<String> p =emptyFunction(
                obj1,obj2
                ,       Arrays.stream(o).
                        collect(Collectors.toSet())
        );
        BeanUtils.copyProperties(obj1,obj2,p.toArray(new String[]{}));

        // localDate identify them and parse them into string
        Set<PropertyDescriptor> LocalDateAttributes = localDatePropertyNameGive(obj1,obj2,p);
        try {
            execute(LocalDateAttributes,obj1,obj2);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }




}
