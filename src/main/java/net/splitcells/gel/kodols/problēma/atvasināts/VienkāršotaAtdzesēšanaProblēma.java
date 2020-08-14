package net.splitcells.gel.kodols.problēma.atvasināts;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.kodols.ierobežojums.Ierobežojums;
import net.splitcells.gel.kodols.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.kodols.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.kodols.atrisinājums.Atrisinājums;
import net.splitcells.gel.kodols.novērtējums.tips.Optimālums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;
import static net.splitcells.gel.kodols.ierobežojums.tips.Atvasināšana.atvasināšana;

public class VienkāršotaAtdzesēšanaProblēma extends AtvasinātsAtrisinājums {

    public static Atrisinājums vienkāršotsAtzesēšanasProblēma(Atrisinājums atrisinājums) {
        return vienkāršsotsAtdzesēšanasProblēma(atrisinājums, i ->
                1f / (i.floatValue() + 1f));
    }
    public static Atrisinājums vienkāršsotsAtdzesēšanasProblēma(Atrisinājums atrisinājums, Function<Integer, Float> temperatureFunction) {
        return new VienkāršotaAtdzesēšanaProblēma(atrisinājums.piešķiršanas(), atrisinājums.ierobežojums(), temperatureFunction);
    }

    protected VienkāršotaAtdzesēšanaProblēma(Piešķiršanas piešķiršanas, Ierobežojums originalIerobežojums, Function<Integer, Float> temperatureFunction) {
        super(() -> list(), piešķiršanas);
        ierobežojums = atvasināšana(originalIerobežojums,
                new Function<>() {
                    private final Randomness randomness = randomness();
                    @Override
                    public RefleksijaNovērtējums apply(RefleksijaNovērtējums rating) {
                        if (randomness.truthValue(temperatureFunction.apply(VienkāršotaAtdzesēšanaProblēma.this.vēsture().izmērs()))) {
                            return Optimālums.optimālums(1).kāReflektētsNovērtējums();
                        }
                        return rating;
                    }
                });
    }
}
