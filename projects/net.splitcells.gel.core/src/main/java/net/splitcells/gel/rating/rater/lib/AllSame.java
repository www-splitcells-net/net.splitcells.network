/*
 * Copyright (c) 2021 Contributors To The `net.splitcells.*` Projects
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License v2.0 or later
 * which is available at https://www.gnu.org/licenses/old-licenses/gpl-2.0-standalone.html
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
package net.splitcells.gel.rating.rater.lib;

import net.splitcells.dem.data.order.Comparators;
import net.splitcells.dem.data.set.map.Map;
import net.splitcells.dem.lang.tree.Tree;
import net.splitcells.gel.constraint.GroupId;
import net.splitcells.gel.data.table.Line;
import net.splitcells.gel.data.table.Table;
import net.splitcells.gel.data.table.attribute.Attribute;
import net.splitcells.gel.proposal.Proposal;
import net.splitcells.gel.rating.framework.Rating;
import net.splitcells.gel.rating.rater.framework.GroupRater;
import net.splitcells.gel.rating.rater.framework.Rater;
import net.splitcells.gel.rating.rater.framework.SimpleDescriptor;

import java.util.Optional;

import static net.splitcells.dem.data.set.Sets.toSetOfUniques;
import static net.splitcells.dem.data.set.list.Lists.toList;
import static net.splitcells.dem.data.set.map.Maps.map;
import static net.splitcells.dem.lang.tree.TreeI.perspective;
import static net.splitcells.dem.utils.NotImplementedYet.notImplementedYet;
import static net.splitcells.gel.constraint.Constraint.LINE;
import static net.splitcells.gel.rating.rater.lib.RaterBasedOnLineGroup.groupRater;
import static net.splitcells.gel.rating.type.Cost.cost;
import static net.splitcells.gel.rating.type.Cost.noCost;

/**
 * Checks whether the {@link Line}s of a group have the same value for a given {@link Attribute}.
 */
public class AllSame {

    public static final String ALL_SAME_NAME = "allSame";

    public static <T> Rater allSame(Attribute<T> attribute) {
        return groupRater(new GroupRater() {

                              @Override
                              public Rating lineRating(Table lines, Optional<Line> addition, Optional<Line> removal) {
                                  final Map<T, Integer> valueCounter = map();
                                  lines.unorderedLines()
                                          .stream()
                                          .filter(e -> removal.map(line -> e.index() != line.index()).orElse(true))
                                          .map(line -> line.value(LINE).value(attribute))
                                          .forEach(value -> {
                                              valueCounter.computeIfPresent(value, (k, v) -> valueCounter.ensurePresenceAndValue(k, v + 1));
                                              valueCounter.computeIfAbsent(value, v -> valueCounter.put(v, 1));
                                          });
                                  if (1 == valueCounter.size()) {
                                      return noCost();
                                  }
                                  final int futureLineSize;
                                  if (removal.isPresent()) {
                                      futureLineSize = lines.size() - 1;
                                  } else {
                                      futureLineSize = lines.size();
                                  }
                                  if (0 == futureLineSize) {
                                      return noCost();
                                  }
                                  final var valueCounts = valueCounter.values()
                                          .stream()
                                          .sorted(Comparators.ASCENDING_INTEGERS)
                                          .collect(toList());
                                  valueCounts.remove(valueCounts.size() - 1);
                                  return cost((double) valueCounts.stream()
                                          .reduce(Integer::sum)
                                          .orElseThrow()
                                          / (double) futureLineSize);
                              }

                              @Override
                              public String toString() {
                                  return "values of " + attribute.name() + " should have the same value";
                              }

                              @Override
                              public Proposal propose(Proposal proposal) {
                                  final var values = proposal.proposedAllocations().unorderedLinesStream()
                                          .map(l -> l.value(attribute))
                                          .collect(toSetOfUniques());
                                  proposal.conextAllocations().unorderedLinesStream()
                                          .forEach(l -> values.add(l.value(attribute)));
                                  if (values.size() == 1) {
                                      final var value = values.stream().findFirst().orElseThrow();
                                      proposal.proposedAllocations().demandsFree().unorderedLines().forEach(df -> {
                                          final var fittingSupplies = proposal.subject()
                                                  .suppliesFree()
                                                  .lookup(attribute, value);
                                          if (fittingSupplies.hasContent()) {
                                              final var fittingSupply = fittingSupplies.unorderedLinesStream()
                                                      .findFirst()
                                                      .orElseThrow();
                                              final var proposedSupply = proposal.proposedAllocations().suppliesFree().add(fittingSupply);
                                              proposal.proposedAllocations().assign(df, proposedSupply);
                                          }
                                      });
                                  } else if (values.size() > 1) {
                                      throw notImplementedYet();
                                  }
                                  return proposal;
                              }
                          },
                new SimpleDescriptor() {
                    @Override
                    public String toSimpleDescription(Line line, Table groupLineProcessing, GroupId incomingGroup) {
                        final var distinctValues = groupLineProcessing.unorderedLines().stream()
                                .map(l -> l.value(LINE).value(attribute).toString())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("undefined");
                        return "values of " + attribute.name() + " should have the same value, but has the values '" + distinctValues + "'.";
                    }

                    @Override
                    public Tree toPerspective() {
                        return perspective("all-same").withProperty("attribute", attribute.toTree());
                    }
                }
        );
    }

    private AllSame() {
    }
}
