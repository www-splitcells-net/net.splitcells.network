package net.splitcells.gel.rating.structure;

import java.util.List;

import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.constraint.Constraint;

public class LocalRatingI implements LocalRating {

	private GroupId radītsIerobežojumuGrupaId;
	private Rating novērtējums;
	private List<Constraint> izdalīUz;

	public static LocalRating lokalsNovērtejums() {
		return new LocalRatingI();
	}

	private LocalRatingI() {

	}

	@Override
	public GroupId radītsIerobežojumuGrupaId() {
		return radītsIerobežojumuGrupaId;
	}

	@Override
	public Rating novērtējums() {
		return novērtējums;
	}

	@Override
	public List<Constraint> izdalīUz() {
		return izdalīUz;
	}

	@Override
	public LocalRatingI arIzdalīšanaUz(List<Constraint> IzdalīšanaUz) {
		this.izdalīUz = IzdalīšanaUz;
		return this;
	}

	@Override
	public LocalRatingI arNovērtējumu(Rating novērtējums) {
		this.novērtējums = novērtējums;
		return this;
	}

	@Override
	public LocalRatingI arRadītuGrupasId(GroupId radītsIerobežojumuGrupaId) {
		this.radītsIerobežojumuGrupaId = radītsIerobežojumuGrupaId;
		return this;
	}

}
