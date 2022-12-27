package hr.fer.oprpp1.custom.collections;

/**
 * Public interface for implementing getter for elements. Contains three methods hasNextElement() and getNextElement() and
 * processRemaining()
 */
public interface ElementsGetter<T> {
     /**
      * Checks if next element exists
      * @return true / false
      */
     boolean hasNextElement();

     /**
      * Gets next element if it exists
      * @return next Object
      */
     T getNextElement();

     /**
      * Takes Processor object and processes all remaining elements using the given Processor
      * @param p processor
      */
     default void  processRemaining(Processor<? super T> p){
          while(this.hasNextElement()){
               p.process(this.getNextElement());
          }
     }
}
