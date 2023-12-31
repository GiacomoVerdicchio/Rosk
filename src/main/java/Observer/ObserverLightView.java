package Observer;

/**
 * It's used because you have also to use an observer pattern between the updated lightView and the actual cli view printed
 */
public interface ObserverLightView {
    void update(Object o);
}