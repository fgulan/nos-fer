//
//  Tuple.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Tuple.h"

@interface Tuple ()

@property (nonatomic, strong) id first;
@property (nonatomic, strong) id second;

@end

@implementation Tuple

+ (instancetype)tupleWithFirst:(id)first second:(id)second
{
    Tuple *tuple = [[Tuple alloc] init];
    tuple.first = first;
    tuple.second = second;
    return tuple;
}

- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

- (BOOL)isEqual:(id)other
{
    if (other == self) {
        return YES;
    } else if (![other isKindOfClass:Tuple.class]) {
        return NO;
    } else {
        Tuple *obj = (Tuple *)other;
        return [obj.first isEqual:self.first] && [obj.second isEqual:self.second];
    }
}

- (NSUInteger)hash
{
    return [self.first hash] ^ [self.second hash];
}

- (NSString *)description
{
    return [NSString stringWithFormat:@"%@ %@", self.first, self.second];
}

- (NSString *)debugDescription
{
    return [NSString stringWithFormat:@"<%@: %p> %@ %@", [self class], self, self.first, self.second];
}

@end
