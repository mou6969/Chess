
package ChessCore;

public interface ChessGameObservable {
    void addObserver(ChessGameObserver observer);
    void removeObserver(ChessGameObserver observer);
    void notifyObservers();
}

