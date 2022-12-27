package hr.fer.oprpp1.custom.collections;

/**
 * Custom stack implementation. Based on ArrayIndexedCollection
 */
public class ObjectStack<T> {
    private final ArrayIndexedCollection<T> stack;

    /**
     * Public constructor
     */
    public ObjectStack() {
        this.stack = new ArrayIndexedCollection<>();
    }


    /**
     * Method that returns boolean saying if stack is empty or not
     * @return boolean: true if stack is empty, false if there's something left on the stack
     */
  public  boolean isEmpty() {//– same as  ArrayIndexedCollection.isEmpty()
        return stack.isEmpty();
    }

    /**
     * Use this method to find out how many of elements there is on the stack
     * @return stack size
     */
   public int size(){
        return stack.size();
    }

    /**
     * Puts one parameter on the stack
     * @param value value to be put on the stack
     */
    public void push(T value) {
        stack.add(value);
    }

    /**
     * Takes one element from the stack
     * @return stack element
     */
    public T pop() {
        T obj = peek();
        stack.remove(stack.size() - 1);
        return obj;
    }

    /**
     * Peaks at the top of the stack, doesn't remove it
     * @return element from the top of the stack
     */
    public  T peek(){
        try {
            return stack.get(stack.size() - 1);
        }catch (IndexOutOfBoundsException e){
            throw new EmptyStackException("There's no elements on the stack");
        }
     }

    /**
     * Removes everything from the stack
     */
    public void clear(){
        stack.clear();
      }


}
