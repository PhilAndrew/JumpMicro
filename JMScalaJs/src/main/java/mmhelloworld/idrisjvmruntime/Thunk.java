package mmhelloworld.idrisjvmruntime;

import java.util.concurrent.Callable;

public interface Thunk extends Callable<Object> {
    Object call();
}
