package simulation;

import simulation.annotations.MyAutowired;
import simulation.service.AService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模拟Spring解决循环依赖的问题
 * @author huangfu
 */
public class DebugTest {

    /**
     * 已经完全创建好的
     */
    private final Map<String,Object> singletonObject = new HashMap<>(8);
    /**
     * 创建一半但是没有属性注入的
     */
    private final Map<String,Object> earlySingletonObjects = new HashMap<>(8);

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        DebugTest debugTest = new DebugTest();
        AService bean = debugTest.getBean(AService.class);
        System.out.println(bean);
    }

    /**
     * 获取一个bean对象
     * @param tClass
     * @return
     */
    public <T> T getBean(Class<T> tClass) throws InstantiationException, IllegalAccessException {
        //先查询一级缓存是否有数据
        String beanName = getBeanName(tClass);
        Object object = singletonObject.get(beanName);
        //一级缓存没有在查询二级缓存是否有数据
        if(object == null){
            object = earlySingletonObjects.get(beanName);
            if(object == null) {
            	//两个缓存都没有就创建类
                object = createBean(tClass,beanName);
            }
        }
        return (T)object;
    }

    /**
     * 创建一个bean
     * @param tClass
     * @param beanName
     * @return
     */
    public Object createBean(Class<?> tClass,String beanName) throws IllegalAccessException, InstantiationException {
        //反射创建对象
        Object newInstance = tClass.newInstance();
        //实例化完就放到二级缓存
        earlySingletonObjects.put(beanName,newInstance);
        //开始填充属性
        populateBean(newInstance);
        //填充完成后从创作中的集合转移到完全体集合
        earlySingletonObjects.remove(beanName);
        singletonObject.put(beanName,newInstance);
        return newInstance;
    }

    /**
     * 填充属性
     */
    public void populateBean(Object object) throws InstantiationException, IllegalAccessException {
    	//获取所有添加了 @MyAutowired 注解的属性
        List<Field> autowiredFields = getAutowiredField(object.getClass());
        for (Field field : autowiredFields) {
        	//开始注入
            doPopulateBean(object, field);
        }
    }

    /**
     * 开始注入对象
     * @param object
     * @param field
     */
    public void doPopulateBean(Object object, Field field) throws IllegalAccessException, InstantiationException {
    	//重新调用获取逻辑
        Object target = getBean(field.getType());
        field.setAccessible(true);
        //反射注入
        field.set(object,target);
    }

    /**
     * 获取被标识自动注入的属性
     * @param tClass
     * @return
     */
    private List<Field> getAutowiredField(Class<?> tClass){
        Field[] declaredFields = tClass.getDeclaredFields();
        return Arrays.stream(declaredFields).filter(field -> field.isAnnotationPresent(MyAutowired.class)).collect(Collectors.toList());
    }
    /**
     * 获取类名称
     * @param tClass
     * @return
     */
    public String getBeanName(Class<?> tClass){
        return tClass.getSimpleName();
    }

}
