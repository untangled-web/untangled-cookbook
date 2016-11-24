-- :name all-people :? :*
SELECT id, name, age, address FROM person;

-- :name get-person :? :1
select id, name, age, address FROM person WHERE id = :id