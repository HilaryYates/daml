// Copyright (c) 2021 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package com.daml.lf.speedy

import com.daml.bazeltools.BazelRunfiles.rlocation
import com.daml.ledger.test.ModelTestDar
import com.daml.lf.archive.DarDecoder
import com.daml.lf.archive.Dar
import com.daml.lf.data.Ref.PackageId
import com.daml.lf.language.Ast.Package
import com.daml.lf.language.PackageInterface
import com.daml.lf.speedy
import java.io.File
import org.openjdk.jmh.annotations.{Param, Setup, Level, Benchmark, State, Scope}

@State(Scope.Benchmark)
class SpeedyCompilation {

  @Param(Array(""))
  var darPath: String = _

  private var dar: Dar[(PackageId, Package)] = _
  private var darMap: Map[PackageId, Package] = _
  private var interface: PackageInterface = _

  @Setup(Level.Trial)
  def setup(): Unit = {
    val darFile = new File(
      if (darPath.isEmpty)
        rlocation(ModelTestDar.path)
      else darPath
    )
    dar = DarDecoder.assertReadArchiveFromFile(darFile)
    darMap = dar.all.toMap
    interface = PackageInterface(darMap)
  }

  @Benchmark
  def bench(): Unit = {
    val config = speedy.Compiler.Config.Default
    val res = speedy.Compiler.compilePackages(interface, darMap, config)
    assert(res.isRight)
  }
}
