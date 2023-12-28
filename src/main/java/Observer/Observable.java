package Observer;

import java.util.ArrayList;
import java.util.List;

public class Observable {

    private final List<Observer> observers = new ArrayList<>();
    private final List<ObserverLightView> observer1s = new ArrayList<>();

    public void addObserver(Observer observer){
        synchronized (observers) {
            observers.add(observer);
        }
    }
    public void addObserverLight(ObserverLightView observer){
        synchronized (observer1s) {
            observer1s.add(observer);
        }
    }

    public void removeObserver(Observer observer){
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public void notify(String message){
        synchronized (observers) {
            for(Observer observer : observers){
                observer.update(message);
            }
        }
    }
    public void notifyLight(Object o){
        synchronized (observer1s) {
            for(ObserverLightView observer : observer1s){
                observer.update(o);
            }
        }
    }
}