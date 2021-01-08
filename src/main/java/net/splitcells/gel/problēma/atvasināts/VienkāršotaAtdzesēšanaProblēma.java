package net.splitcells.gel.problēma.atvasināts;

import net.splitcells.dem.utils.random.Randomness;
import net.splitcells.gel.atrisinājums.Atrisinājums;
import net.splitcells.gel.dati.piešķiršanas.Piešķiršanas;
import net.splitcells.gel.ierobežojums.Ierobežojums;
import net.splitcells.gel.ierobežojums.tips.Atvasināšana;
import net.splitcells.gel.novērtējums.struktūra.RefleksijaNovērtējums;
import net.splitcells.gel.novērtējums.tips.Optimālums;

import java.util.function.Function;

import static net.splitcells.dem.data.set.list.Lists.list;
import static net.splitcells.dem.utils.random.RandomnessSource.randomness;

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
        ierobežojums = Atvasināšana.atvasināšana(originalIerobežojums,
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
