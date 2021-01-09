package net.splitcells.gel.rating.structure;

import java.util.List;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public interface LocalRating {

	GroupId radītsIerobežojumuGrupaId();

	Rating novērtējums();

	List<Constraint> izdalīUz();

	LocalRatingI arIzdalīšanaUz(List<Constraint> IzdalīšanaUz);

	LocalRatingI arNovērtējumu(Rating novērtējums);

	LocalRatingI arRadītuGrupasId(GroupId radītsIerobežojumuGrupaId);
}
