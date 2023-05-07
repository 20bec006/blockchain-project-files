package observable.interfaces;

import visualizer.VisualizerEvent;

public interface IVisualizerEventSubject {

  void register(IVisualizerEventObserver o);

  void unregister(IVisualizerEventObserver o);

  void notifyObserver(VisualizerEvent e);
}
