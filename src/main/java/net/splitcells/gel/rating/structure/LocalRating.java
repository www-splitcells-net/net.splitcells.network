package net.splitcells.gel.rating.structure;

import java.util.List;

import net.splitcells.gel.constraint.GrupaId;
import net.splitcells.gel.constraint.Ierobežojums;

public interface LocalRating {

	GrupaId radītsIerobežojumuGrupaId();

	Rating novērtējums();

	List<Ierobežojums> izdalīUz();

	LocalRatingI arIzdalīšanaUz(List<Ierobežojums> IzdalīšanaUz);

	LocalRatingI arNovērtējumu(Rating novērtējums);

	LocalRatingI arRadītuGrupasId(GrupaId radītsIerobežojumuGrupaId);
}
