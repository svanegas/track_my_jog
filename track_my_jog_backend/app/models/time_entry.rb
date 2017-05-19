class TimeEntry < ApplicationRecord
  # Associations
  belongs_to :user

  # Validations
  validates :distance, :duration, :user, presence: true
  validates :distance, :duration, numericality: { greater_than: 0 }
  validate :date_on_or_before_today

  private

  def date_on_or_before_today
    if self.date.nil?
      errors.add(:date, :blank)
    elsif self.date > Date.today
      errors.add(:date, :cant_be_future)
    end
  end
end
