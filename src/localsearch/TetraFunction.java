package localsearch;

/**
 * {@link FunctionalInterface} for functions accepting 4 parameters and returning one.
 *
 * @author Ondřej Kratochvíl
 */
@FunctionalInterface
public interface TetraFunction<A1, A2, A3, A4, R> {

    R apply(A1 a1, A2 a2, A3 a3, A4 a4);
}
