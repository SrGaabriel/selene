trait Collection(
    size(self) :: int32,
    get(self, index: int32) :: string,
    push(mut self, value: string) :: void,
)
