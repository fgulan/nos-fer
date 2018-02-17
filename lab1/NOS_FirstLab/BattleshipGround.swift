//
//  BattleshipGround.swift
//  NOS
//
//  Created by Filip Gulan on 16/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

import Foundation

@objc
public class GroundSize: NSObject {
    let rows: UInt32
    let cols: UInt32

    init(rows: UInt32, cols: UInt32) {
        self.rows = rows
        self.cols = cols
    }
}

@objc
public class ShipPosition: NSObject {
    let row: UInt32
    let col: UInt32

    init(row: UInt32, col: UInt32) {
        self.row = row
        self.col = col
    }

    public override func isEqual(_ object: Any?) -> Bool {
        guard let position = object as? ShipPosition else {
            return false
        }
        if position === self {
            return true
        }

        return position.row == self.row && position.col == self.col
    }
}

@objc
public class BattleshipGround: NSObject {

    let size: GroundSize
    let shipsCount: UInt32
    fileprivate var ships: [ShipPosition] = []
    var isFinished: Bool {
        return ships.isEmpty
    }

    init(size: GroundSize, shipsCount: UInt32) {
        self.size = size
        if shipsCount >= (size.cols * size.rows) {
            self.shipsCount = (size.cols * size.rows) - 1
        } else {
            self.shipsCount = shipsCount
        }
        super.init()
        self.ships = _generateField(with: self.shipsCount, for: size)
    }

    fileprivate func _generateField(with count: UInt32, for size: GroundSize) -> [ShipPosition] {
        var ships: [ShipPosition] = []
        while ships.count < Int(count) {
            let position = _generatePostion(for: size)
            if !ships.contains(where: { $0 == position }) {
                ships.append(position)
            }
        }
        return ships
    }

    fileprivate func _generatePostion(for size: GroundSize) -> ShipPosition {
        let row: UInt32 = arc4random_uniform(size.rows)
        let col: UInt32 = arc4random_uniform(size.cols)
        return ShipPosition(row: row, col: col)
    }

    func fire(at position: ShipPosition) -> Bool {
        var hit: Bool = false
        if let index = ships.index(of: position) {
            ships.remove(at: index)
            hit = true
        }
        return hit
    }

    func fireAt(row: UInt32, col: UInt32) -> Bool {
        return fire(at: ShipPosition(row: row, col: col))
    }

    override public var description: String {
        var field: String = ""
        (0..<size.rows).forEach { row in
            let line = (0..<size.cols).map({ col -> String in
                let position = ShipPosition(row: row, col: col)
                return ships.contains(where: { $0 == position }) ? "o" : "-"
            }).joined(separator: " ")
            if row == size.rows - 1 {
                field += "\(line)"
            } else {
                field += "\(line)\n"
            }
        }
        return field
    }
}
